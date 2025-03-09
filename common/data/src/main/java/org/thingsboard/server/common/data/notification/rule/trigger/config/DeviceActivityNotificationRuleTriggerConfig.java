
package org.thingsboard.server.common.data.notification.rule.trigger.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceActivityNotificationRuleTriggerConfig implements NotificationRuleTriggerConfig {

    private Set<UUID> devices;
    private Set<UUID> deviceProfiles; // set either devices or profiles
    @NotEmpty
    private Set<DeviceEvent> notifyOn;

    @Override
    public NotificationRuleTriggerType getTriggerType() {
        return NotificationRuleTriggerType.DEVICE_ACTIVITY;
    }

    public enum DeviceEvent {
        ACTIVE, INACTIVE
    }

}
