
package org.thingsboard.server.common.data.notification;

import lombok.Data;
import org.thingsboard.server.common.data.notification.template.DeliveryMethodNotificationTemplate;

import java.util.Collection;
import java.util.Map;

@Data
public class NotificationRequestPreview {

    private Map<NotificationDeliveryMethod, DeliveryMethodNotificationTemplate> processedTemplates;
    private int totalRecipientsCount;
    private Map<String, Integer> recipientsCountByTarget;
    private Collection<String> recipientsPreview;

}
