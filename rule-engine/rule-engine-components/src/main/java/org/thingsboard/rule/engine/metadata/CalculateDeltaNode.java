
package org.thingsboard.rule.engine.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNode;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.data.msg.TbNodeConnectionType;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.dao.timeseries.TimeseriesService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.thingsboard.common.util.DonAsynchron.withCallback;

@Slf4j
@RuleNode(type = ComponentType.ENRICHMENT,
        name = "calculate delta", relationTypes = {TbNodeConnectionType.SUCCESS, TbNodeConnectionType.FAILURE, TbNodeConnectionType.OTHER},
        configClazz = CalculateDeltaNodeConfiguration.class,
        nodeDescription = "Calculates delta and amount of time passed between previous timeseries key reading " +
                "and current value for this key from the incoming message",
        nodeDetails = "Useful for metering use cases, when you need to calculate consumption based on pulse counter reading.<br><br>" +
                "Output connections: <code>Success</code>, <code>Other</code> or <code>Failure</code>.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbEnrichmentNodeCalculateDeltaConfig")
public class CalculateDeltaNode implements TbNode {

    private Map<EntityId, ValueWithTs> cache;
    private CalculateDeltaNodeConfiguration config;
    private TbContext ctx;
    private TimeseriesService timeseriesService;
    private boolean useCache;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, CalculateDeltaNodeConfiguration.class);
        this.ctx = ctx;
        this.timeseriesService = ctx.getTimeseriesService();
        this.useCache = config.isUseCache();
        if (useCache) {
            cache = new ConcurrentHashMap<>();
        }
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        if (!msg.isTypeOf(TbMsgType.POST_TELEMETRY_REQUEST)) {
            ctx.tellNext(msg, TbNodeConnectionType.OTHER);
            return;
        }
        JsonNode json = JacksonUtil.toJsonNode(msg.getData());
        String inputKey = config.getInputValueKey();
        if (!json.has(inputKey)) {
            ctx.tellNext(msg, TbNodeConnectionType.OTHER);
            return;
        }
        withCallback(getLastValue(msg.getOriginator()),
                previousData -> {
                    double currentValue = json.get(inputKey).asDouble();
                    long currentTs = msg.getMetaDataTs();

                    if (useCache) {
                        cache.put(msg.getOriginator(), new ValueWithTs(currentTs, currentValue));
                    }

                    BigDecimal delta = BigDecimal.valueOf(previousData != null ? currentValue - previousData.value : 0.0);

                    if (config.isTellFailureIfDeltaIsNegative() && delta.doubleValue() < 0) {
                        ctx.tellFailure(msg, new IllegalArgumentException("Delta value is negative!"));
                        return;
                    }

                    if (config.getRound() != null) {
                        delta = delta.setScale(config.getRound(), RoundingMode.HALF_UP);
                    }

                    ObjectNode result = (ObjectNode) json;
                    if (delta.stripTrailingZeros().scale() > 0) {
                        result.put(config.getOutputValueKey(), delta.doubleValue());
                    } else {
                        result.put(config.getOutputValueKey(), delta.longValueExact());
                    }

                    if (config.isAddPeriodBetweenMsgs()) {
                        long period = previousData != null ? currentTs - previousData.ts : 0;
                        result.put(config.getPeriodValueKey(), period);
                    }
                    ctx.tellSuccess(TbMsg.transformMsgData(msg, JacksonUtil.toString(result)));
                },
                t -> ctx.tellFailure(msg, t), ctx.getDbCallbackExecutor());
    }

    @Override
    public void destroy() {
        if (useCache) {
            cache.clear();
        }
    }

    private ListenableFuture<ValueWithTs> fetchLatestValueAsync(EntityId entityId) {
        return Futures.transform(timeseriesService.findLatest(ctx.getTenantId(), entityId, Collections.singletonList(config.getInputValueKey())),
                list -> extractValue(list.get(0))
                , ctx.getDbCallbackExecutor());
    }

    private ValueWithTs fetchLatestValue(EntityId entityId) {
        List<TsKvEntry> tsKvEntries = timeseriesService.findLatestSync(
                ctx.getTenantId(),
                entityId,
                Collections.singletonList(config.getInputValueKey()));
        return extractValue(tsKvEntries.get(0));
    }

    private ListenableFuture<ValueWithTs> getLastValue(EntityId entityId) {
        if (useCache) {
            ValueWithTs latestValue;
            if ((latestValue = cache.get(entityId)) == null) {
                latestValue = fetchLatestValue(entityId);
            }
            return Futures.immediateFuture(latestValue);
        } else {
            return fetchLatestValueAsync(entityId);
        }
    }

    private ValueWithTs extractValue(TsKvEntry kvEntry) {
        if (kvEntry == null || kvEntry.getValue() == null) {
            return null;
        }
        double result = 0.0;
        long ts = kvEntry.getTs();
        switch (kvEntry.getDataType()) {
            case LONG:
                result = kvEntry.getLongValue().get();
                break;
            case DOUBLE:
                result = kvEntry.getDoubleValue().get();
                break;
            case STRING:
                try {
                    result = Double.parseDouble(kvEntry.getStrValue().get());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Calculation failed. Unable to parse value [" + kvEntry.getStrValue().get() + "]" +
                            " of telemetry [" + kvEntry.getKey() + "] to Double");
                }
                break;
            case BOOLEAN:
                throw new IllegalArgumentException("Calculation failed. Boolean values are not supported!");
            case JSON:
                throw new IllegalArgumentException("Calculation failed. JSON values are not supported!");
        }
        return new ValueWithTs(ts, result);
    }

    private static class ValueWithTs {
        private final long ts;
        private final double value;

        private ValueWithTs(long ts, double value) {
            this.ts = ts;
            this.value = value;
        }
    }

}
