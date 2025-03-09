
package org.thingsboard.server.common.data.notification.settings;

import lombok.Data;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;

import javax.validation.constraints.NotEmpty;

@Data
public class MobileAppNotificationDeliveryMethodConfig implements NotificationDeliveryMethodConfig {

    private String firebaseServiceAccountCredentialsFileName;
    @NotEmpty
    private String firebaseServiceAccountCredentials;

    @Override
    public NotificationDeliveryMethod getMethod() {
        return NotificationDeliveryMethod.MOBILE_APP;
    }

}
