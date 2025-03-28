
package org.thingsboard.server.dao.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.NotificationRequestId;
import org.thingsboard.server.common.data.id.NotificationRuleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.NotificationRequest;
import org.thingsboard.server.common.data.notification.NotificationRequestInfo;
import org.thingsboard.server.common.data.notification.NotificationRequestStats;
import org.thingsboard.server.common.data.notification.NotificationRequestStatus;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;
import org.thingsboard.server.dao.service.DataValidator;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultNotificationRequestService implements NotificationRequestService, EntityDaoService {

    private final NotificationRequestDao notificationRequestDao;
    private final NotificationDao notificationDao;

    private final NotificationRequestValidator notificationRequestValidator = new NotificationRequestValidator();

    @Override
    public NotificationRequest saveNotificationRequest(TenantId tenantId, NotificationRequest notificationRequest) {
        notificationRequestValidator.validate(notificationRequest, NotificationRequest::getTenantId);
        return notificationRequestDao.save(tenantId, notificationRequest);
    }

    @Override
    public NotificationRequest findNotificationRequestById(TenantId tenantId, NotificationRequestId id) {
        return notificationRequestDao.findById(tenantId, id.getId());
    }

    @Override
    public NotificationRequestInfo findNotificationRequestInfoById(TenantId tenantId, NotificationRequestId id) {
        return notificationRequestDao.findInfoById(tenantId, id);
    }

    @Override
    public PageData<NotificationRequest> findNotificationRequestsByTenantIdAndOriginatorType(TenantId tenantId, EntityType originatorType, PageLink pageLink) {
        return notificationRequestDao.findByTenantIdAndOriginatorTypeAndPageLink(tenantId, originatorType, pageLink);
    }

    @Override
    public PageData<NotificationRequestInfo> findNotificationRequestsInfosByTenantIdAndOriginatorType(TenantId tenantId, EntityType originatorType, PageLink pageLink) {
        return notificationRequestDao.findInfosByTenantIdAndOriginatorTypeAndPageLink(tenantId, originatorType, pageLink);
    }

    @Override
    public List<NotificationRequestId> findNotificationRequestsIdsByStatusAndRuleId(TenantId tenantId, NotificationRequestStatus requestStatus, NotificationRuleId ruleId) {
        return notificationRequestDao.findIdsByRuleId(tenantId, requestStatus, ruleId);
    }

    @Override
    public List<NotificationRequest> findNotificationRequestsByRuleIdAndOriginatorEntityId(TenantId tenantId, NotificationRuleId ruleId, EntityId originatorEntityId) {
        return notificationRequestDao.findByRuleIdAndOriginatorEntityId(tenantId, ruleId, originatorEntityId);
    }

    @Override
    public void deleteNotificationRequest(TenantId tenantId, NotificationRequestId requestId) {
        notificationRequestDao.removeById(tenantId, requestId.getId());
        notificationDao.deleteByRequestId(tenantId, requestId);
    }

    @Override
    public PageData<NotificationRequest> findScheduledNotificationRequests(PageLink pageLink) {
        return notificationRequestDao.findAllByStatus(NotificationRequestStatus.SCHEDULED, pageLink);
    }

    @Override
    public void updateNotificationRequest(TenantId tenantId, NotificationRequestId requestId, NotificationRequestStatus requestStatus, NotificationRequestStats stats) {
        notificationRequestDao.updateById(tenantId, requestId, requestStatus, stats);
    }

    // notifications themselves are left in the database until removed by ttl
    @Override
    public void deleteNotificationRequestsByTenantId(TenantId tenantId) {
        notificationRequestDao.removeByTenantId(tenantId);
    }

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findNotificationRequestById(tenantId, new NotificationRequestId(entityId.getId())));
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION_REQUEST;
    }


    private static class NotificationRequestValidator extends DataValidator<NotificationRequest> {

    }

}
