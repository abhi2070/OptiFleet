
package org.thingsboard.server.common.data.notification.rule.trigger.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.alarm.AlarmSearchStatus;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmNotificationRuleTriggerConfig implements NotificationRuleTriggerConfig {

    private Set<String> alarmTypes;
    private Set<AlarmSeverity> alarmSeverities;
    @NotEmpty
    private Set<AlarmAction> notifyOn;

    private ClearRule clearRule;

    @Override
    public NotificationRuleTriggerType getTriggerType() {
        return NotificationRuleTriggerType.ALARM;
    }

    @Data
    public static class ClearRule implements Serializable {
        private Set<AlarmSearchStatus> alarmStatuses;
    }

    public enum AlarmAction {
        CREATED, SEVERITY_CHANGED, ACKNOWLEDGED, CLEARED
    }

}
