
package org.thingsboard.server.service.entity.alarm;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmComment;
import org.thingsboard.server.common.data.exception.ThingsboardException;

public interface TbAlarmCommentService {

    AlarmComment saveAlarmComment(Alarm alarm, AlarmComment alarmComment, User user) throws ThingsboardException;

    void deleteAlarmComment(Alarm alarm, AlarmComment alarmComment, User user) throws ThingsboardException;
}
