
package org.thingsboard.server.dao.sql.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.NotificationId;
import org.thingsboard.server.common.data.id.NotificationRequestId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.common.data.notification.NotificationStatus;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.sql.NotificationEntity;
import org.thingsboard.server.dao.notification.NotificationDao;
import org.thingsboard.server.dao.sql.JpaPartitionedAbstractDao;
import org.thingsboard.server.dao.sqlts.insert.sql.SqlPartitioningRepository;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@SqlDao
@RequiredArgsConstructor
public class JpaNotificationDao extends JpaPartitionedAbstractDao<NotificationEntity, Notification> implements NotificationDao {

    private final NotificationRepository notificationRepository;
    private final SqlPartitioningRepository partitioningRepository;

    @Value("${sql.notifications.partition_size:168}")
    private int partitionSizeInHours;

    @Override
    public PageData<Notification> findUnreadByRecipientIdAndPageLink(TenantId tenantId, UserId recipientId, PageLink pageLink) {
        return DaoUtil.toPageData(notificationRepository.findByRecipientIdAndStatusNot(recipientId.getId(), NotificationStatus.READ,
                pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<Notification> findByRecipientIdAndPageLink(TenantId tenantId, UserId recipientId, PageLink pageLink) {
        return DaoUtil.toPageData(notificationRepository.findByRecipientId(recipientId.getId(),
                pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public boolean updateStatusByIdAndRecipientId(TenantId tenantId, UserId recipientId, NotificationId notificationId, NotificationStatus status) {
        return notificationRepository.updateStatusByIdAndRecipientId(notificationId.getId(), recipientId.getId(), status) != 0;
    }

    /**
     * For this hot method, the partial index `idx_notification_recipient_id_unread` was introduced since 3.6.0
     * */
    @Override
    public int countUnreadByRecipientId(TenantId tenantId, UserId recipientId) {
        return notificationRepository.countByRecipientIdAndStatusNot(recipientId.getId(), NotificationStatus.READ);
    }

    @Override
    public PageData<Notification> findByRequestId(TenantId tenantId, NotificationRequestId notificationRequestId, PageLink pageLink) {
        return DaoUtil.toPageData(notificationRepository.findByRequestId(notificationRequestId.getId(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public boolean deleteByIdAndRecipientId(TenantId tenantId, UserId recipientId, NotificationId notificationId) {
        return notificationRepository.deleteByIdAndRecipientId(notificationId.getId(), recipientId.getId()) != 0;
    }

    @Override
    public int updateStatusByRecipientId(TenantId tenantId, UserId recipientId, NotificationStatus status) {
        return notificationRepository.updateStatusByRecipientId(recipientId.getId(), status);
    }

    @Override
    public void deleteByRequestId(TenantId tenantId, NotificationRequestId requestId) {
        notificationRepository.deleteByRequestId(requestId.getId());
    }

    @Override
    public void deleteByRecipientId(TenantId tenantId, UserId recipientId) {
        notificationRepository.deleteByRecipientId(recipientId.getId());
    }

    @Override
    public void createPartition(NotificationEntity entity) {
        partitioningRepository.createPartitionIfNotExists(ModelConstants.NOTIFICATION_TABLE_NAME,
                entity.getCreatedTime(), TimeUnit.HOURS.toMillis(partitionSizeInHours));
    }

    @Override
    protected Class<NotificationEntity> getEntityClass() {
        return NotificationEntity.class;
    }

    @Override
    protected JpaRepository<NotificationEntity, UUID> getRepository() {
        return notificationRepository;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION;
    }

}
