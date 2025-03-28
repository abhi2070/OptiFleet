
package org.thingsboard.server.service.edge.rpc.processor.alarm;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.alarm.AlarmStatus;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.AlarmUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class AlarmEdgeProcessorV1 extends AlarmEdgeProcessor {

    @Override
    protected Alarm constructAlarmFromUpdateMsg(TenantId tenantId, AlarmId alarmId, EntityId originatorId, AlarmUpdateMsg alarmUpdateMsg) {
        Alarm alarm = new Alarm();
        alarm.setId(alarmId);
        alarm.setTenantId(tenantId);
        alarm.setOriginator(originatorId);
        alarm.setType(alarmUpdateMsg.getName());
        alarm.setSeverity(AlarmSeverity.valueOf(alarmUpdateMsg.getSeverity()));
        alarm.setStartTs(alarmUpdateMsg.getStartTs());
        AlarmStatus alarmStatus = AlarmStatus.valueOf(alarmUpdateMsg.getStatus());
        alarm.setClearTs(alarmUpdateMsg.getClearTs());
        alarm.setPropagate(alarmUpdateMsg.getPropagate());
        alarm.setCleared(alarmStatus.isCleared());
        alarm.setAcknowledged(alarmStatus.isAck());
        alarm.setAckTs(alarmUpdateMsg.getAckTs());
        alarm.setEndTs(alarmUpdateMsg.getEndTs());
        alarm.setDetails(JacksonUtil.toJsonNode(alarmUpdateMsg.getDetails()));
        return alarm;
    }

    @Override
    protected EntityId getAlarmOriginatorFromMsg(TenantId tenantId, AlarmUpdateMsg alarmUpdateMsg) {
        return getAlarmOriginator(tenantId, alarmUpdateMsg.getOriginatorName(),
                EntityType.valueOf(alarmUpdateMsg.getOriginatorType()));
    }

    private EntityId getAlarmOriginator(TenantId tenantId, String entityName, EntityType entityType) {
        switch (entityType) {
            case DEVICE:
                return deviceService.findDeviceByTenantIdAndName(tenantId, entityName).getId();
            case ASSET:
                return assetService.findAssetByTenantIdAndName(tenantId, entityName).getId();
            case ENTITY_VIEW:
                return entityViewService.findEntityViewByTenantIdAndName(tenantId, entityName).getId();
            default:
                return null;
        }
    }
}
