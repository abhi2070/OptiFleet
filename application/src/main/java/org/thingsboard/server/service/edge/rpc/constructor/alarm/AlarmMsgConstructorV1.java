
package org.thingsboard.server.service.edge.rpc.constructor.alarm;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.gen.edge.v1.AlarmUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class AlarmMsgConstructorV1 extends BaseAlarmMsgConstructor {

    @Override
    public AlarmUpdateMsg constructAlarmUpdatedMsg(UpdateMsgType msgType, Alarm alarm, String entityName) {
        return AlarmUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(alarm.getId().getId().getMostSignificantBits())
                .setIdLSB(alarm.getId().getId().getLeastSignificantBits())
                .setName(alarm.getName())
                .setType(alarm.getType())
                .setOriginatorName(entityName)
                .setOriginatorType(alarm.getOriginator().getEntityType().name())
                .setSeverity(alarm.getSeverity().name())
                .setStatus(alarm.getStatus().name())
                .setStartTs(alarm.getStartTs())
                .setEndTs(alarm.getEndTs())
                .setAckTs(alarm.getAckTs())
                .setClearTs(alarm.getClearTs())
                .setDetails(JacksonUtil.toString(alarm.getDetails()))
                .setPropagate(alarm.isPropagate())
                .setPropagateToOwner(alarm.isPropagateToOwner())
                .setPropagateToTenant(alarm.isPropagateToTenant()).build();
    }
}
