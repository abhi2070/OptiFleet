
package org.thingsboard.server.common.data.notification.template;

import lombok.Data;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

@Data
public class NotificationTemplateConfig {

    @Valid
    @NotEmpty
    private Map<NotificationDeliveryMethod, DeliveryMethodNotificationTemplate> deliveryMethodsTemplates;

    public NotificationTemplateConfig copy() {
        Map<NotificationDeliveryMethod, DeliveryMethodNotificationTemplate> templates = new HashMap<>(deliveryMethodsTemplates);
        templates.replaceAll((deliveryMethod, template) -> template.copy());
        NotificationTemplateConfig copy = new NotificationTemplateConfig();
        copy.setDeliveryMethodsTemplates(templates);
        return copy;
    }

}
