
package org.thingsboard.server.dao.notification;

import org.thingsboard.server.common.data.id.NotificationTemplateId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.notification.template.NotificationTemplate;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.List;

public interface NotificationTemplateService {

    NotificationTemplate findNotificationTemplateById(TenantId tenantId, NotificationTemplateId id);

    NotificationTemplate saveNotificationTemplate(TenantId tenantId, NotificationTemplate notificationTemplate);

    PageData<NotificationTemplate> findNotificationTemplatesByTenantIdAndNotificationTypes(TenantId tenantId, List<NotificationType> notificationTypes, PageLink pageLink);

    void deleteNotificationTemplateById(TenantId tenantId, NotificationTemplateId id);

    void deleteNotificationTemplatesByTenantId(TenantId tenantId);

}
