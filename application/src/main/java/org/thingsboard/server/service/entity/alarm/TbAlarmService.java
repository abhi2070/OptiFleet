
package org.thingsboard.server.service.entity.alarm;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;

import java.util.List;

public interface TbAlarmService {

    Alarm save(Alarm entity, User user) throws ThingsboardException;

    AlarmInfo ack(Alarm alarm, User user) throws ThingsboardException;

    AlarmInfo ack(Alarm alarm, long ackTs, User user) throws ThingsboardException;

    AlarmInfo clear(Alarm alarm, User user) throws ThingsboardException;

    AlarmInfo clear(Alarm alarm, long clearTs, User user) throws ThingsboardException;

    AlarmInfo assign(Alarm alarm, UserId assigneeId, long assignTs, User user) throws ThingsboardException;

    AlarmInfo unassign(Alarm alarm, long unassignTs, User user) throws ThingsboardException;

    List<AlarmId> unassignDeletedUserAlarms(TenantId tenantId, User user, long unassignTs);

    Boolean delete(Alarm alarm, User user);
}
