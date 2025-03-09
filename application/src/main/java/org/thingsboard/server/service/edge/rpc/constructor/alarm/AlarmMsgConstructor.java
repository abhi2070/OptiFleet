
package org.thingsboard.server.service.edge.rpc.constructor.alarm;

import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmComment;
import org.thingsboard.server.gen.edge.v1.AlarmCommentUpdateMsg;
import org.thingsboard.server.gen.edge.v1.AlarmUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface AlarmMsgConstructor extends MsgConstructor {

    AlarmUpdateMsg constructAlarmUpdatedMsg(UpdateMsgType msgType, Alarm alarm, String entityName);

    AlarmCommentUpdateMsg constructAlarmCommentUpdatedMsg(UpdateMsgType msgType, AlarmComment alarmComment);
}
