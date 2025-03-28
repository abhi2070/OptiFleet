
package org.thingsboard.server.service.stats;

import com.google.common.util.concurrent.FutureCallback;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.BasicTsKvEntry;
import org.thingsboard.server.common.data.kv.JsonDataEntry;
import org.thingsboard.server.common.data.kv.LongDataEntry;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import org.thingsboard.server.dao.asset.AssetService;
import org.thingsboard.server.dao.usagerecord.ApiLimitService;
import org.thingsboard.server.queue.discovery.TbServiceInfoProvider;
import org.thingsboard.server.queue.util.TbRuleEngineComponent;
import org.thingsboard.server.service.queue.TbRuleEngineConsumerStats;
import org.thingsboard.server.service.telemetry.TelemetrySubscriptionService;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@TbRuleEngineComponent
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultRuleEngineStatisticsService implements RuleEngineStatisticsService {

    public static final String TB_SERVICE_QUEUE = "TbServiceQueue";
    public static final String RULE_ENGINE_EXCEPTION = "ruleEngineException";
    public static final FutureCallback<Integer> CALLBACK = new FutureCallback<Integer>() {
        @Override
        public void onSuccess(@Nullable Integer result) {

        }

        @Override
        public void onFailure(Throwable t) {
            log.warn("Failed to persist statistics", t);
        }
    };

    private final TbServiceInfoProvider serviceInfoProvider;
    private final TelemetrySubscriptionService tsService;
    private final AssetService assetService;
    private final ApiLimitService apiLimitService;
    private final Lock lock = new ReentrantLock();
    private final ConcurrentMap<TenantQueueKey, AssetId> tenantQueueAssets = new ConcurrentHashMap<>();

    @Value("${queue.rule-engine.stats.max-error-message-length:4096}")
    private int maxErrorMessageLength;

    @Override
    public void reportQueueStats(long ts, TbRuleEngineConsumerStats ruleEngineStats) {
        String queueName = ruleEngineStats.getQueueName();
        ruleEngineStats.getTenantStats().forEach((id, stats) -> {
            try {
                TenantId tenantId = TenantId.fromUUID(id);
                AssetId serviceAssetId = getServiceAssetId(tenantId, queueName);
                if (stats.getTotalMsgCounter().get() > 0) {
                    List<TsKvEntry> tsList = stats.getCounters().entrySet().stream()
                            .map(kv -> new BasicTsKvEntry(ts, new LongDataEntry(kv.getKey(), (long) kv.getValue().get())))
                            .collect(Collectors.toList());
                    if (!tsList.isEmpty()) {
                        long ttl = apiLimitService.getLimit(tenantId, DefaultTenantProfileConfiguration::getQueueStatsTtlDays);
                        ttl = TimeUnit.DAYS.toSeconds(ttl);
                        tsService.saveAndNotifyInternal(tenantId, serviceAssetId, tsList, ttl, CALLBACK);
                    }
                }
            } catch (Exception e) {
                if (!"Asset is referencing to non-existent tenant!".equalsIgnoreCase(e.getMessage())) {
                    log.debug("[{}] Failed to store the statistics", id, e);
                }
            }
        });
        ruleEngineStats.getTenantExceptions().forEach((tenantId, e) -> {
            try {
                TsKvEntry tsKv = new BasicTsKvEntry(e.getTs(), new JsonDataEntry(RULE_ENGINE_EXCEPTION, e.toJsonString(maxErrorMessageLength)));
                long ttl = apiLimitService.getLimit(tenantId, DefaultTenantProfileConfiguration::getRuleEngineExceptionsTtlDays);
                ttl = TimeUnit.DAYS.toSeconds(ttl);
                tsService.saveAndNotifyInternal(tenantId, getServiceAssetId(tenantId, queueName), Collections.singletonList(tsKv), ttl, CALLBACK);
            } catch (Exception e2) {
                if (!"Asset is referencing to non-existent tenant!".equalsIgnoreCase(e2.getMessage())) {
                    log.debug("[{}] Failed to store the statistics", tenantId, e2);
                }
            }
        });
    }

    private AssetId getServiceAssetId(TenantId tenantId, String queueName) {
        TenantQueueKey key = new TenantQueueKey(tenantId, queueName);
        AssetId assetId = tenantQueueAssets.get(key);
        if (assetId == null) {
            lock.lock();
            try {
                assetId = tenantQueueAssets.get(key);
                if (assetId == null) {
                    Asset asset = assetService.findAssetByTenantIdAndName(tenantId, queueName + "_" + serviceInfoProvider.getServiceId());
                    if (asset == null) {
                        asset = new Asset();
                        asset.setTenantId(tenantId);
                        asset.setName(queueName + "_" + serviceInfoProvider.getServiceId());
                        asset.setType(TB_SERVICE_QUEUE);
                        asset = assetService.saveAsset(asset);
                    }
                    assetId = asset.getId();
                    tenantQueueAssets.put(key, assetId);
                }
            } finally {
                lock.unlock();
            }
        }
        return assetId;
    }

    @Data
    private static class TenantQueueKey {
        private final TenantId tenantId;
        private final String queueName;
    }
}
