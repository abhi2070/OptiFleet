
package org.thingsboard.server.dao.sql.notification;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.NotificationRuleId;
import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.rule.NotificationRule;
import org.thingsboard.server.common.data.notification.rule.NotificationRuleInfo;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.NotificationRuleEntity;
import org.thingsboard.server.dao.model.sql.NotificationRuleInfoEntity;
import org.thingsboard.server.dao.notification.NotificationRuleDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@SqlDao
@RequiredArgsConstructor
public class JpaNotificationRuleDao extends JpaAbstractDao<NotificationRuleEntity, NotificationRule> implements NotificationRuleDao {

    private final NotificationRuleRepository notificationRuleRepository;

    @Override
    public PageData<NotificationRule> findByTenantIdAndPageLink(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(notificationRuleRepository.findByTenantIdAndSearchText(tenantId.getId(),
                pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<NotificationRuleInfo> findInfosByTenantIdAndPageLink(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(notificationRuleRepository.findInfosByTenantIdAndSearchText(tenantId.getId(),
                        pageLink.getTextSearch(), DaoUtil.toPageable(pageLink, Map.of(
                                "templateName", "t.name"
                        ))))
                .mapData(NotificationRuleInfoEntity::toData);
    }

    @Override
    public boolean existsByTenantIdAndTargetId(TenantId tenantId, NotificationTargetId targetId) {
        return notificationRuleRepository.existsByTenantIdAndRecipientsConfigContaining(tenantId.getId(), targetId.getId().toString());
    }

    @Override
    public List<NotificationRule> findByTenantIdAndTriggerTypeAndEnabled(TenantId tenantId, NotificationRuleTriggerType triggerType, boolean enabled) {
        return DaoUtil.convertDataList(notificationRuleRepository.findAllByTenantIdAndTriggerTypeAndEnabled(tenantId.getId(), triggerType, enabled));
    }

    @Override
    public NotificationRuleInfo findInfoById(TenantId tenantId, NotificationRuleId id) {
        NotificationRuleInfoEntity infoEntity = notificationRuleRepository.findInfoById(id.getId());
        return infoEntity != null ? infoEntity.toData() : null;
    }

    @Override
    public void removeByTenantId(TenantId tenantId) {
        notificationRuleRepository.deleteByTenantId(tenantId.getId());
    }

    @Override
    public NotificationRule findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
//        return DaoUtil.getData(notificationRuleRepository.findByTenantIdAndExternalId(tenantId, externalId));
        return null;
    }

    @Override
    public NotificationRule findByTenantIdAndName(UUID tenantId, String name) {
        return DaoUtil.getData(notificationRuleRepository.findByTenantIdAndName(tenantId, name));
    }

    @Override
    public PageData<NotificationRule> findByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(notificationRuleRepository.findByTenantId(tenantId, DaoUtil.toPageable(pageLink)));
    }

    @Override
    public NotificationRuleId getExternalIdByInternal(NotificationRuleId internalId) {
        return DaoUtil.toEntityId(notificationRuleRepository.getExternalIdByInternal(internalId.getId()), NotificationRuleId::new);
    }

    @Override
    protected Class<NotificationRuleEntity> getEntityClass() {
        return NotificationRuleEntity.class;
    }

    @Override
    protected JpaRepository<NotificationRuleEntity, UUID> getRepository() {
        return notificationRuleRepository;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION_RULE;
    }

}
