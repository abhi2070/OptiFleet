
package org.thingsboard.server.common.data.notification.settings;

import lombok.Data;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Data
public class NotificationSettings implements Serializable {

    @NotNull
    @Valid
    private Map<NotificationDeliveryMethod, NotificationDeliveryMethodConfig> deliveryMethodsConfigs;

}
