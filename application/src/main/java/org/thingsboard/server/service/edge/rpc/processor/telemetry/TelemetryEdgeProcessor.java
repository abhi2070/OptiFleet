
package org.thingsboard.server.service.edge.rpc.processor.telemetry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EntityDataProto;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Slf4j
@Component
@TbCoreComponent
public class TelemetryEdgeProcessor extends BaseTelemetryProcessor {

    @Override
    protected String getMsgSourceKey() {
        return DataConstants.EDGE_MSG_SOURCE;
    }

    public DownlinkMsg convertTelemetryEventToDownlink(EdgeEvent edgeEvent) {
        if (edgeEvent.getBody() != null) {
            String bodyStr = edgeEvent.getBody().toString();
            if (bodyStr.length() > 1000) {
                log.debug("[{}][{}][{}] Conversion to a DownlinkMsg telemetry event failed due to a size limit violation. " +
                                "Current size is {}, but the limit is 1000. {}", edgeEvent.getTenantId(), edgeEvent.getEdgeId(),
                        edgeEvent.getEntityId(), bodyStr.length(), StringUtils.truncate(bodyStr, 100));
                return null;
            }
        }
        EntityType entityType = EntityType.valueOf(edgeEvent.getType().name());
        EntityDataProto entityDataProto = convertTelemetryEventToEntityDataProto(
                edgeEvent.getTenantId(), entityType, edgeEvent.getEntityId(),
                edgeEvent.getAction(), edgeEvent.getBody());
        return DownlinkMsg.newBuilder()
                .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                .addEntityData(entityDataProto)
                .build();
    }
}
