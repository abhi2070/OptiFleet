
package org.thingsboard.server.service.notification.rule.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.rule.NotificationRule;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;
import org.thingsboard.server.common.data.plugin.ComponentLifecycleEvent;
import org.thingsboard.server.common.msg.plugin.ComponentLifecycleMsg;
import org.thingsboard.server.dao.notification.NotificationRuleService;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultNotificationRulesCache implements NotificationRulesCache {

    private final NotificationRuleService notificationRuleService;

    @Value("${cache.notificationRules.maxSize:1000}")
    private int cacheMaxSize;
    @Value("${cache.notificationRules.timeToLiveInMinutes:30}")
    private int cacheValueTtl;
    private Cache<String, List<NotificationRule>> cache;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @PostConstruct
    private void init() {
        cache = Caffeine.newBuilder()
                .maximumSize(cacheMaxSize)
                .expireAfterAccess(cacheValueTtl, TimeUnit.MINUTES)
                .build();
    }

    @EventListener(ComponentLifecycleMsg.class)
    public void onComponentLifecycleEvent(ComponentLifecycleMsg event) {
        switch (event.getEntityId().getEntityType()) {
            case NOTIFICATION_RULE:
                evict(event.getTenantId()); // TODO: evict by trigger type of the rule
                break;
            case TENANT:
                if (event.getEvent() == ComponentLifecycleEvent.DELETED) {
                    lock.writeLock().lock(); // locking in case rules for tenant are fetched while evicting
                    try {
                        evict(event.getTenantId());
                    } finally {
                        lock.writeLock().unlock();
                    }
                }
                break;
        }
    }

    @Override
    public List<NotificationRule> getEnabled(TenantId tenantId, NotificationRuleTriggerType triggerType) {
        lock.readLock().lock();
        try {
            log.trace("Retrieving notification rules of type {} for tenant {} from cache", triggerType, tenantId);
            return cache.get(key(tenantId, triggerType), k -> {
                List<NotificationRule> rules = notificationRuleService.findEnabledNotificationRulesByTenantIdAndTriggerType(tenantId, triggerType);
                log.trace("Fetched notification rules of type {} for tenant {} (count: {})", triggerType, tenantId, rules.size());
                return !rules.isEmpty() ? rules : Collections.emptyList();
            });
        } finally {
            lock.readLock().unlock();
        }
    }

    public void evict(TenantId tenantId) {
        cache.invalidateAll(Arrays.stream(NotificationRuleTriggerType.values())
                .map(triggerType -> key(tenantId, triggerType))
                .collect(Collectors.toList()));
        log.trace("Evicted all notification rules for tenant {} from cache", tenantId);
    }

    private static String key(TenantId tenantId, NotificationRuleTriggerType triggerType) {
        return tenantId + "_" + triggerType;
    }

}
