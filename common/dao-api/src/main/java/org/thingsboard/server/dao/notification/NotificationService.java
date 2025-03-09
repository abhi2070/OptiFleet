
package org.thingsboard.server.dao.notification;

import org.thingsboard.server.common.data.id.NotificationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

public interface NotificationService {

    Notification saveNotification(TenantId tenantId, Notification notification);

    Notification findNotificationById(TenantId tenantId, NotificationId notificationId);

    boolean markNotificationAsRead(TenantId tenantId, UserId recipientId, NotificationId notificationId);

    int markAllNotificationsAsRead(TenantId tenantId, UserId recipientId);

    PageData<Notification> findNotificationsByRecipientIdAndReadStatus(TenantId tenantId, UserId recipientId, boolean unreadOnly, PageLink pageLink);

    PageData<Notification> findLatestUnreadNotificationsByRecipientId(TenantId tenantId, UserId recipientId, int limit);

    int countUnreadNotificationsByRecipientId(TenantId tenantId, UserId recipientId);

    boolean deleteNotification(TenantId tenantId, UserId recipientId, NotificationId notificationId);

}
