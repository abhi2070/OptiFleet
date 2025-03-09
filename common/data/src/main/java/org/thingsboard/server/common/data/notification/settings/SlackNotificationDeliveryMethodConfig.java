
package org.thingsboard.server.common.data.notification.settings;

import lombok.Data;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;

import javax.validation.constraints.NotEmpty;

@Data
public class SlackNotificationDeliveryMethodConfig implements NotificationDeliveryMethodConfig {

    @NotEmpty
    private String botToken;

    @Override
    public NotificationDeliveryMethod getMethod() {
        return NotificationDeliveryMethod.SLACK;
    }

}
