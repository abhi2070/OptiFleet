
package org.thingsboard.server.common.data.notification.rule.trigger.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.alarm.AlarmSearchStatus;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmAssignmentNotificationRuleTriggerConfig implements NotificationRuleTriggerConfig {

    private Set<String> alarmTypes;
    private Set<AlarmSeverity> alarmSeverities;
    private Set<AlarmSearchStatus> alarmStatuses;
    @NotEmpty
    private Set<Action> notifyOn;

    @Override
    public NotificationRuleTriggerType getTriggerType() {
        return NotificationRuleTriggerType.ALARM_ASSIGNMENT;
    }

    public enum Action {
        ASSIGNED, UNASSIGNED
    }

}
