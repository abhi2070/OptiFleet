
package org.thingsboard.server.service.notification.rule.trigger;

import org.thingsboard.server.common.data.notification.info.RuleOriginatedNotificationInfo;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerConfig;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;
import org.thingsboard.server.common.data.notification.rule.trigger.NotificationRuleTrigger;

public interface NotificationRuleTriggerProcessor<T extends NotificationRuleTrigger, C extends NotificationRuleTriggerConfig> {

    boolean matchesFilter(T trigger, C triggerConfig);

    default boolean matchesClearRule(T trigger, C triggerConfig) {
        return false;
    }

    RuleOriginatedNotificationInfo constructNotificationInfo(T trigger);

    NotificationRuleTriggerType getTriggerType();

}
