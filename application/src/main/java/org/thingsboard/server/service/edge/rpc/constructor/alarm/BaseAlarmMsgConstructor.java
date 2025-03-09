
package org.thingsboard.server.service.edge.rpc.constructor.alarm;

import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.alarm.AlarmComment;
import org.thingsboard.server.gen.edge.v1.AlarmCommentUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;

public abstract class BaseAlarmMsgConstructor implements AlarmMsgConstructor {

    @Override
    public AlarmCommentUpdateMsg constructAlarmCommentUpdatedMsg(UpdateMsgType msgType, AlarmComment alarmComment) {
        return AlarmCommentUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(alarmComment)).build();
    }
}
