
package org.thingsboard.server.dao.entity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultEntityServiceRegistry implements EntityServiceRegistry {

    private final ApplicationContext applicationContext;
    private final Map<EntityType, EntityDaoService> entityDaoServicesMap = new HashMap<>();

    @EventListener(ContextRefreshedEvent.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void init() {
        log.debug("Initializing EntityServiceRegistry on ContextRefreshedEvent");
        applicationContext.getBeansOfType(EntityDaoService.class).values().forEach(entityDaoService -> {
            EntityType entityType = entityDaoService.getEntityType();
            entityDaoServicesMap.put(entityType, entityDaoService);
            if (EntityType.RULE_CHAIN.equals(entityType)) {
                entityDaoServicesMap.put(EntityType.RULE_NODE, entityDaoService);
            }
        });
        log.debug("Initialized EntityServiceRegistry total [{}] entries", entityDaoServicesMap.size());
    }

    @Override
    public EntityDaoService getServiceByEntityType(EntityType entityType) {
        return entityDaoServicesMap.get(entityType);
    }

}
