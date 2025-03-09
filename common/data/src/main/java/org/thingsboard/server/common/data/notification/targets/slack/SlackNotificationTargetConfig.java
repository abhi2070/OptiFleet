
package org.thingsboard.server.common.data.notification.targets.slack;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.notification.targets.NotificationTargetConfig;
import org.thingsboard.server.common.data.notification.targets.NotificationTargetType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class SlackNotificationTargetConfig extends NotificationTargetConfig {

    private SlackConversationType conversationType;
    @NotNull
    @Valid
    private SlackConversation conversation;

    @Override
    public NotificationTargetType getType() {
        return NotificationTargetType.SLACK;
    }

}
