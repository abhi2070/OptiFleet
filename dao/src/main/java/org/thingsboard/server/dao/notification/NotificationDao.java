
package org.thingsboard.server.dao.notification;

import org.thingsboard.server.common.data.id.NotificationId;
import org.thingsboard.server.common.data.id.NotificationRequestId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.common.data.notification.NotificationStatus;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

public interface NotificationDao extends Dao<Notification> {

    PageData<Notification> findUnreadByRecipientIdAndPageLink(TenantId tenantId, UserId recipientId, PageLink pageLink);

    PageData<Notification> findByRecipientIdAndPageLink(TenantId tenantId, UserId recipientId, PageLink pageLink);

    boolean updateStatusByIdAndRecipientId(TenantId tenantId, UserId recipientId, NotificationId notificationId, NotificationStatus status);

    int countUnreadByRecipientId(TenantId tenantId, UserId recipientId);

    PageData<Notification> findByRequestId(TenantId tenantId, NotificationRequestId notificationRequestId, PageLink pageLink);

    boolean deleteByIdAndRecipientId(TenantId tenantId, UserId recipientId, NotificationId notificationId);

    int updateStatusByRecipientId(TenantId tenantId, UserId recipientId, NotificationStatus status);

    void deleteByRequestId(TenantId tenantId, NotificationRequestId requestId);

    void deleteByRecipientId(TenantId tenantId, UserId recipientId);

}
