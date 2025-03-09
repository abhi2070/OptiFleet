
package org.thingsboard.rule.engine.api;

import com.google.common.util.concurrent.FutureCallback;
import org.thingsboard.server.common.data.id.NotificationId;
import org.thingsboard.server.common.data.id.NotificationRequestId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.notification.NotificationRequest;
import org.thingsboard.server.common.data.notification.NotificationRequestStats;
import org.thingsboard.server.common.data.notification.targets.platform.UsersFilter;
import org.thingsboard.server.common.data.notification.template.NotificationTemplate;

import java.util.Set;

public interface NotificationCenter {

    NotificationRequest processNotificationRequest(TenantId tenantId, NotificationRequest notificationRequest, FutureCallback<NotificationRequestStats> callback);

    void sendGeneralWebNotification(TenantId tenantId, UsersFilter recipients, NotificationTemplate template);

    void deleteNotificationRequest(TenantId tenantId, NotificationRequestId notificationRequestId);

    void markNotificationAsRead(TenantId tenantId, UserId recipientId, NotificationId notificationId);

    void markAllNotificationsAsRead(TenantId tenantId, UserId recipientId);

    void deleteNotification(TenantId tenantId, UserId recipientId, NotificationId notificationId);

    Set<NotificationDeliveryMethod> getAvailableDeliveryMethods(TenantId tenantId);

}
