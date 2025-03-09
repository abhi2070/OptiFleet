package org.thingsboard.server.dao.integration;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.integration.IntegrationInfo;
import org.thingsboard.server.common.data.integration.IntegrationSearchQuery;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.dao.entity.AbstractCachedEntityService;
import org.thingsboard.server.dao.entity.EntityCountService;
import org.thingsboard.server.dao.eventsourcing.ActionEntityEvent;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.DaoUtil.toUUIDs;
import static org.thingsboard.server.dao.service.Validator.*;

@Service("IntegrationDaoService")
@Slf4j
public class BaseIntegrationService extends AbstractCachedEntityService<IntegrationCacheKey, Integration, IntegrationCacheEvictEvent> implements IntegrationService{

    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";

    public static final String INCORRECT_CUSTOMER_ID = "Incorrect customerId ";
    public static final String INCORRECT_INTEGRATION_ID = "Incorrect integrationId ";
    public static final String TB_SERVICE_QUEUE = "TbServiceQueue";

    @Autowired
    private IntegrationDao integrationDao;

    @Autowired
    private DataValidator<Integration> integrationValidator;

    @Autowired
    private EntityCountService countService;

    @TransactionalEventListener(classes = IntegrationCacheEvictEvent.class)
    @Override
    public void handleEvictEvent(IntegrationCacheEvictEvent event) {
        List<IntegrationCacheKey> keys = new ArrayList<>(2);
        keys.add(new IntegrationCacheKey(event.getTenantId(), event.getNewName()));
        if (StringUtils.isNotEmpty(event.getOldName()) && !event.getOldName().equals(event.getNewName())) {
            keys.add(new IntegrationCacheKey(event.getTenantId(), event.getOldName()));
        }
        cache.evict(keys);
    }

    @Override
    public IntegrationInfo findIntegrationInfoById(TenantId tenantId, IntegrationId integrationId) {
        validateId(integrationId, INCORRECT_INTEGRATION_ID + integrationId);
        return integrationDao.findIntegrationInfoById(tenantId, integrationId.getId());
    }

    @Override
    public Integration findIntegrationById(TenantId tenantId, IntegrationId integrationId) {
        validateId(integrationId, INCORRECT_INTEGRATION_ID + integrationId);
        return integrationDao.findById(tenantId, integrationId.getId());
    }

    @Override
    public ListenableFuture<Integration> findIntegrationByIdAsync(TenantId tenantId, IntegrationId integrationId) {
        validateId(integrationId, INCORRECT_INTEGRATION_ID + integrationId);
        return integrationDao.findByIdAsync(tenantId, integrationId.getId());
    }

    @Override
    public Integration findIntegrationByTenantIdAndName(TenantId tenantId, String name) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        return cache.getAndPutInTransaction(new IntegrationCacheKey(tenantId, name),
                () -> integrationDao.findIntegrationsByTenantIdAndName(tenantId.getId(), name)
                        .orElse(null), true);
    }

    @Override
    public Integration saveIntegration(Integration integration, boolean doValidate) {
        return doSaveIntegration(integration, doValidate);
    }

    @Override

    public Integration saveIntegration(Integration integration) {
        return doSaveIntegration(integration, true);
    }
    @Transactional
    private Integration doSaveIntegration(Integration integration, boolean doValidate) {
        Integration iCheck = new Integration();

        Integration oldIntegration = null;
        if (doValidate) {
            oldIntegration = integrationValidator.validate(integration, Integration::getTenantId);
        } else if (integration.getId() != null) {
            oldIntegration = findIntegrationById(integration.getTenantId(), integration.getId());
        }
        IntegrationCacheEvictEvent evictEvent = new IntegrationCacheEvictEvent(integration.getTenantId(), integration.getName(), oldIntegration != null ? oldIntegration.getName() : null);
        Integration savedIntegration;
        try {
            savedIntegration = integrationDao.saveAndFlush(integration.getTenantId(), integration);
            publishEvictEvent(evictEvent);
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(savedIntegration.getTenantId())
                    .entityId(savedIntegration.getId()).created(integration.getId() == null).build());
            if (integration.getId() == null) {
                countService.publishCountEntityEvictEvent(savedIntegration.getTenantId(), EntityType.INTEGRATION);
            }
        } catch (Exception t) {
            handleEvictEvent(evictEvent);
            checkConstraintViolation(t,
                    "integration_name_unq_key", "integration with such name already exists!",
                    "integration_external_id_unq_key", "integration with such external id already exists!");
            throw t;
        }
        return savedIntegration;
    }

    @Override
    public Integration assignIntegrationToCustomer(TenantId tenantId, IntegrationId integrationId, CustomerId customerId) {
        Integration integration = findIntegrationById(tenantId, integrationId);
        integration.setCustomerId(customerId);
        return saveIntegration(integration);
    }

    @Override
    public Integration unassignIntegrationFromCustomer(TenantId tenantId, IntegrationId integrationId) {
        Integration integration = findIntegrationById(tenantId, integrationId);
        integration.setCustomerId(null);
        return saveIntegration(integration);
    }

    @Override
    public void deleteIntegration(TenantId tenantId, IntegrationId integrationId) {
        validateId(integrationId, INCORRECT_INTEGRATION_ID + integrationId);
        if (entityViewService.existsByTenantIdAndEntityId(tenantId, integrationId)) {
            throw new DataValidationException("Can't delete integration that has entity views!");
        }

        Integration integration = integrationDao.findById(tenantId, integrationId.getId());
        alarmService.deleteEntityAlarmRelations(tenantId, integrationId);
        deleteIntegration(tenantId, integration);
    }

    private void deleteIntegration(TenantId tenantId, Integration integration) {
        relationService.deleteEntityRelations(tenantId, integration.getId());

        integrationDao.removeById(tenantId, integration.getUuidId());

        publishEvictEvent(new IntegrationCacheEvictEvent(integration.getTenantId(), integration.getName(), null));
        countService.publishCountEntityEvictEvent(tenantId, EntityType.INTEGRATION);
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(integration.getId()).build());
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantId(TenantId tenantId, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return integrationDao.findIntegrationsByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return integrationDao.findIntegrationsByTenantIdAndType(tenantId.getId(), type, pageLink);
    }

    @Override
    public PageData<IntegrationInfo> findIntegrationInfosByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return integrationDao.findIntegrationInfosByTenantIdAndType(tenantId.getId(), type, pageLink);
    }

    @Override
    public PageData<IntegrationInfo> findIntegrationInfosByTenantId(TenantId tenantId, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return integrationDao.findIntegrationInfosByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public PageData<Integration> findIntegrations(int pageSize, int page, String sortProperty, SortOrder sortOrder, String type) {
        return integrationDao.findIntegrations(pageSize, page, sortProperty, sortOrder, type);
    }


    @Override
    public ListenableFuture<List<Integration>> findIntegrationsByTenantIdAndIdsAsync(TenantId tenantId, List<IntegrationId> integrationIds) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateIds(integrationIds, "Incorrect integrationIds " + integrationIds);
        return integrationDao.findIntegrationsByTenantIdAndIdsAsync(tenantId.getId(), toUUIDs(integrationIds));
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validatePageLink(pageLink);
        return integrationDao.findIntegrationsByTenantIdAndCustomerId(tenantId.getId(), customerId.getId(), pageLink);
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return integrationDao.findIntegrationsByTenantIdAndCustomerIdAndType(tenantId.getId(), customerId.getId(), type, pageLink);
    }

    @Override
    public ListenableFuture<List<Integration>> findIntegrationsByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<IntegrationId> integrationIds) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validateIds(integrationIds, "Incorrect assetIds " + integrationIds);
        return integrationDao.findIntegrationsByTenantIdAndCustomerIdAndIdsAsync(tenantId.getId(), customerId.getId(), toUUIDs(integrationIds));
    }

    @Override
    public ListenableFuture<List<Integration>> findIntegrationsByQuery(TenantId tenantId, IntegrationSearchQuery query) {
        ListenableFuture<List<EntityRelation>> relations = relationService.findByQuery(tenantId, query.toEntitySearchQuery());
        ListenableFuture<List<Integration>> integrations = Futures.transformAsync(relations, r -> {
            EntitySearchDirection direction = query.toEntitySearchQuery().getParameters().getDirection();
            List<ListenableFuture<Integration>> futures = new ArrayList<>();
            for (EntityRelation relation : r) {
                EntityId entityId = direction == EntitySearchDirection.FROM ? relation.getTo() : relation.getFrom();
                if (entityId.getEntityType() == EntityType.INTEGRATION) {
                    futures.add(findIntegrationByIdAsync(tenantId, new IntegrationId(entityId.getId())));
                }
            }
            return Futures.successfulAsList(futures);
        }, MoreExecutors.directExecutor());
        integrations = Futures.transform(integrations, integrationList ->
                        integrationList == null ?
                                Collections.emptyList() :
                                integrationList.stream()
                                        .filter(integration -> query.getIntegrationTypes().contains(integration.getType()))
                                        .collect(Collectors.toList()),
                MoreExecutors.directExecutor()
        );
        return integrations;
    }

    @Override
    public Integration assignIntegrationToEdge(TenantId tenantId, IntegrationId integrationId, EdgeId edgeId) {
        Integration integration = findIntegrationById(tenantId, integrationId);
        Edge edge = edgeService.findEdgeById(tenantId, edgeId);
        if (edge == null) {
            throw new DataValidationException("Can't assign integration to non-existent edge!");
        }
        if (!edge.getTenantId().getId().equals(integration.getTenantId().getId())) {
            throw new DataValidationException("Can't assign integration to edge from different tenant!");
        }
        try {
            createRelation(tenantId, new EntityRelation(edgeId, integrationId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.EDGE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eventPublisher.publishEvent(ActionEntityEvent.builder().tenantId(tenantId).edgeId(edgeId).entityId(integrationId)
                .actionType(ActionType.ASSIGNED_TO_EDGE).build());
        return integration;
    }

    @Override
    public Integration unassignIntegrationFromEdge(TenantId tenantId, IntegrationId integrationId, EdgeId edgeId) {
        Integration integration = findIntegrationById(tenantId, integrationId);
        Edge edge = edgeService.findEdgeById(tenantId, edgeId);
        if (edge == null) {
            throw new DataValidationException("Can't unassign integration from non-existent edge!");
        }

        checkAssignedEntityViewsToEdge(tenantId, integrationId, edgeId);

        try {
            deleteRelation(tenantId, new EntityRelation(edgeId, integrationId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.EDGE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eventPublisher.publishEvent(ActionEntityEvent.builder().tenantId(tenantId).edgeId(edgeId).entityId(integrationId)
                .actionType(ActionType.UNASSIGNED_FROM_EDGE).build());
        return integration;
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(edgeId, INCORRECT_EDGE_ID + edgeId);
        validatePageLink(pageLink);
        return integrationDao.findIntegrationsByTenantIdAndEdgeId(tenantId.getId(), edgeId.getId(), pageLink);
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(edgeId, INCORRECT_EDGE_ID + edgeId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return integrationDao.findIntegrationsByTenantIdAndEdgeIdAndType(tenantId.getId(), edgeId.getId(), type, pageLink);
    }

    private final PaginatedRemover<TenantId, Integration> tenantIntegrationsRemover = new PaginatedRemover<>() {

        @Override
        protected PageData<Integration> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
            return integrationDao.findIntegrationsByTenantId(id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, Integration integration) {
            deleteIntegration(tenantId, integration);
        }
    };

    private final PaginatedRemover<CustomerId, Integration> customerIntegrationsUnasigner = new PaginatedRemover<CustomerId, Integration>() {

        @Override
        protected PageData<Integration> findEntities(TenantId tenantId, CustomerId id, PageLink pageLink) {
            return integrationDao.findIntegrationsByTenantIdAndCustomerId(tenantId.getId(), id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, Integration entity) {
            unassignIntegrationFromCustomer(tenantId, new IntegrationId(entity.getId().getId()));
        }
    };

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findIntegrationById(tenantId, new IntegrationId(entityId.getId())));
    }

    @Override
    public long countByTenantId(TenantId tenantId) {
        return integrationDao.countByTenantId(tenantId);
    }

    @Override
    @Transactional
    public void deleteEntity(TenantId tenantId, EntityId id) {
        deleteIntegration(tenantId, (IntegrationId) id);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.INTEGRATION;
    }

}
