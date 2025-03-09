
package org.thingsboard.server.service.edge.rpc.constructor.alarm;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.gen.edge.v1.AlarmUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class AlarmMsgConstructorV2 extends BaseAlarmMsgConstructor {

    @Override
    public AlarmUpdateMsg constructAlarmUpdatedMsg(UpdateMsgType msgType, Alarm alarm, String entityName) {
        return AlarmUpdateMsg.newBuilder().setMsgType(msgType)
                .setEntity(JacksonUtil.toString(alarm))
                .setIdMSB(alarm.getId().getId().getMostSignificantBits())
                .setIdLSB(alarm.getId().getId().getLeastSignificantBits()).build();
    }
}
