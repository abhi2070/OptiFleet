
package org.thingsboard.server.dao.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.NotificationRuleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.rule.NotificationRule;
import org.thingsboard.server.common.data.notification.rule.NotificationRuleInfo;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultNotificationRuleService extends AbstractEntityService implements NotificationRuleService, EntityDaoService {

    private final NotificationRuleDao notificationRuleDao;

    @Override
    public NotificationRule saveNotificationRule(TenantId tenantId, NotificationRule notificationRule) {
        if (notificationRule.getId() != null) {
            NotificationRule oldNotificationRule = findNotificationRuleById(tenantId, notificationRule.getId());
            if (notificationRule.getTriggerType() != oldNotificationRule.getTriggerType()) {
                throw new IllegalArgumentException("Notification rule trigger type cannot be updated");
            }
        }
        try {
            return notificationRuleDao.saveAndFlush(tenantId, notificationRule);
        } catch (Exception e) {
            checkConstraintViolation(e, Map.of(
                    "uq_notification_rule_name", "Notification rule with such name already exists"
            ));
            throw e;
        }
    }

    @Override
    public NotificationRule findNotificationRuleById(TenantId tenantId, NotificationRuleId id) {
        return notificationRuleDao.findById(tenantId, id.getId());
    }

    @Override
    public NotificationRuleInfo findNotificationRuleInfoById(TenantId tenantId, NotificationRuleId id) {
        return notificationRuleDao.findInfoById(tenantId, id);
    }

    @Override
    public PageData<NotificationRuleInfo> findNotificationRulesInfosByTenantId(TenantId tenantId, PageLink pageLink) {
        return notificationRuleDao.findInfosByTenantIdAndPageLink(tenantId, pageLink);
    }

    @Override
    public PageData<NotificationRule> findNotificationRulesByTenantId(TenantId tenantId, PageLink pageLink) {
        return notificationRuleDao.findByTenantIdAndPageLink(tenantId, pageLink);
    }

    @Override
    public List<NotificationRule> findEnabledNotificationRulesByTenantIdAndTriggerType(TenantId tenantId, NotificationRuleTriggerType triggerType) {
        return notificationRuleDao.findByTenantIdAndTriggerTypeAndEnabled(tenantId, triggerType, true);
    }

    @Override
    public void deleteNotificationRuleById(TenantId tenantId, NotificationRuleId id) {
        notificationRuleDao.removeById(tenantId, id.getId());
    }

    @Override
    public void deleteNotificationRulesByTenantId(TenantId tenantId) {
        notificationRuleDao.removeByTenantId(tenantId);
    }

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findNotificationRuleById(tenantId, new NotificationRuleId(entityId.getId())));
    }

    @Override
    public void deleteEntity(TenantId tenantId, EntityId id) {
        deleteNotificationRuleById(tenantId, (NotificationRuleId) id);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION_RULE;
    }

}
