
package org.thingsboard.server.common.data.notification.rule.trigger.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.EntityType;

import javax.validation.constraints.Max;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntitiesLimitNotificationRuleTriggerConfig implements NotificationRuleTriggerConfig {

    private Set<EntityType> entityTypes;
    @Max(1)
    private float threshold; // in percents,

    @Override
    public NotificationRuleTriggerType getTriggerType() {
        return NotificationRuleTriggerType.ENTITIES_LIMIT;
    }

}
