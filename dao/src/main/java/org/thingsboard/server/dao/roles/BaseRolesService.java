package org.thingsboard.server.dao.roles;

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
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.roles.RolesInfo;
import org.thingsboard.server.common.data.roles.RolesSearchQuery;
import org.thingsboard.server.dao.entity.AbstractCachedEntityService;
import org.thingsboard.server.dao.entity.EntityCountService;
import org.thingsboard.server.dao.eventsourcing.ActionEntityEvent;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;

import java.util.*;
import java.util.stream.Collectors;

import static org.thingsboard.server.common.data.id.EntityId.NULL_UUID;
import static org.thingsboard.server.dao.service.Validator.*;

@Service("RolesDaoService")
@Slf4j
public class BaseRolesService extends AbstractCachedEntityService<RolesCacheKey, Roles, RolesCacheEvictEvent> implements RolesService{


    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_ROLES_ID = "Incorrect rolesId ";
    public static final String TB_SERVICE_QUEUE = "TbServiceQueue";
    public static final String DUPLICATE_MESSAGE = "such name already exists!";

    @Autowired
    private RolesDao rolesDao;

    @Autowired
    private DataValidator<Roles> rolesValidator;

    @Autowired
    private EntityCountService countService;

    @TransactionalEventListener(classes = RolesCacheEvictEvent.class)
    @Override
    public void handleEvictEvent(RolesCacheEvictEvent event) {
        List<RolesCacheKey> keys = new ArrayList<>(2);
        keys.add(new RolesCacheKey(event.getTenantId(), event.getNewName()));
        if (StringUtils.isNotEmpty(event.getOldName()) && !event.getOldName().equals(event.getNewName())) {
            keys.add(new RolesCacheKey(event.getTenantId(), event.getOldName()));
        }
        cache.evict(keys);
    }





    @Override
    public RolesInfo findRolesInfoById(TenantId tenantId, RolesId rolesId) {
        validateId(rolesId, INCORRECT_ROLES_ID + rolesId);
        return rolesDao.findRolesInfoById(tenantId, rolesId.getId());
    }

    @Override
    public Roles findRolesById(TenantId tenantId, RolesId rolesId) {
        validateId(rolesId, INCORRECT_ROLES_ID + rolesId);
        return rolesDao.findById(tenantId, rolesId.getId());
    }

    @Override
    public ListenableFuture<Roles> findRolesByIdAsync(TenantId tenantId, RolesId rolesId) {
        validateId(rolesId, INCORRECT_ROLES_ID + rolesId);
        return rolesDao.findByIdAsync(tenantId, rolesId.getId());
    }

    @Override
    public Roles findRolesByTenantIdAndName(TenantId tenantId, String name) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        return cache.getAndPutInTransaction(new RolesCacheKey(tenantId, name),
                () -> rolesDao.findRolesByTenantIdAndName(tenantId.getId(), name)
                        .orElse(null), true);
    }

    @Override
    public Roles saveRoles(Roles roles, boolean doValidate) {
        return doSaveRoles(roles, doValidate);
    }

    @Override
    public Roles saveRoles(Roles roles) {

        return doSaveRoles(roles, true);
    }

    private Roles doSaveRoles(Roles roles, boolean doValidate) {

            roles.setCustomerId(new CustomerId(NULL_UUID));

            Roles oldRoles = null;
            if (doValidate) {
                oldRoles = rolesValidator.validate(roles, Roles::getTenantId);
            } else if (roles.getId() != null) {
                oldRoles = findRolesById(roles.getTenantId(), roles.getId());
            }
            RolesCacheEvictEvent evictEvent = new RolesCacheEvictEvent(roles.getTenantId(), roles.getName(), oldRoles != null ? oldRoles.getName() : null);
            Roles savedRoles;
            try {
                savedRoles = rolesDao.saveAndFlush(roles.getTenantId(), roles);
                publishEvictEvent(evictEvent);
                eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(savedRoles.getTenantId())
                        .entityId(savedRoles.getId()).created(roles.getId() == null).build());
                if (roles.getId() == null) {
                    countService.publishCountEntityEvictEvent(savedRoles.getTenantId(), EntityType.ROLES);
                }
            } catch (Exception t) {
                handleEvictEvent(evictEvent);
                checkConstraintViolation(t,
                        "roles_name_unq_key", "Roles with such name already exists!",
                        "roles_external_id_unq_key", "Roles with such external id already exists!");
                throw t;
            }
            return savedRoles;



    }

    @Override
    public Roles unassignRolesFromEdge(TenantId tenantId, RolesId rolesId, EdgeId edgeId) {
        Roles roles = findRolesById(tenantId, rolesId);
        Edge edge = edgeService.findEdgeById(tenantId, edgeId);
        if (edge == null) {
            throw new DataValidationException("Can't unassign roles from non-existent edge!");
        }

        checkAssignedEntityViewsToEdge(tenantId, rolesId, edgeId);

        try {
            deleteRelation(tenantId, new EntityRelation(edgeId, rolesId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.EDGE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eventPublisher.publishEvent(ActionEntityEvent.builder().tenantId(tenantId).edgeId(edgeId).entityId(rolesId)
                .actionType(ActionType.UNASSIGNED_FROM_EDGE).build());
        return roles;
    }



    @Override
    public void deleteRoles(TenantId tenantId, RolesId rolesId) {
        validateId(rolesId, INCORRECT_ROLES_ID + rolesId);
        if (entityViewService.existsByTenantIdAndEntityId(tenantId, rolesId)) {
            throw new DataValidationException("Can't delete roles that has entity views!");
        }

        Roles roles = rolesDao.findById(tenantId, rolesId.getId());
        alarmService.deleteEntityAlarmRelations(tenantId, rolesId);
        deleteRoles(tenantId, roles);
    }
    private void deleteRoles(TenantId tenantId, Roles roles) {
        relationService.deleteEntityRelations(tenantId, roles.getId());
        rolesDao.removeById(tenantId, roles.getUuidId());

        publishEvictEvent(new RolesCacheEvictEvent(roles.getTenantId(), roles.getName(), null));
        countService.publishCountEntityEvictEvent(tenantId, EntityType.ROLES);
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(roles.getId()).build());
    }

    @Override
    public PageData<Roles> findRolesByTenantId(TenantId tenantId, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return rolesDao.findRolesByTenantId(tenantId.getId(), pageLink);
    }


    @Override
    public PageData<Roles> findRolesByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return rolesDao.findRolesByTenantIdAndType(tenantId.getId(), type, pageLink);
    }


    @Override
    public Roles assignRolesToEdge(TenantId tenantId, RolesId rolesId, EdgeId edgeId) {
        Roles roles = findRolesById(tenantId, rolesId);
        Edge edge = edgeService.findEdgeById(tenantId, edgeId);
        if (edge == null) {
            throw new DataValidationException("Can't assign roles to non-existent edge!");
        }
        if (!edge.getTenantId().getId().equals(roles.getTenantId().getId())) {
            throw new DataValidationException("Can't assign roles to edge from different tenant!");
        }
        try {
            createRelation(tenantId, new EntityRelation(edgeId, rolesId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.EDGE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eventPublisher.publishEvent(ActionEntityEvent.builder().tenantId(tenantId).edgeId(edgeId).entityId(rolesId)
                .actionType(ActionType.ASSIGNED_TO_EDGE).build());
        return roles;
    }


    @Override
    public ListenableFuture<List<Roles>> findRolesByQuery(TenantId tenantId, RolesSearchQuery query) {
        ListenableFuture<List<EntityRelation>> relations = relationService.findByQuery(tenantId, query.toEntitySearchQuery());
        ListenableFuture<List<Roles>> role = Futures.transformAsync(relations, r -> {
            EntitySearchDirection direction = query.toEntitySearchQuery().getParameters().getDirection();
            List<ListenableFuture<Roles>> futures = new ArrayList<>();
            for (EntityRelation relation : r) {
                EntityId entityId = direction == EntitySearchDirection.FROM ? relation.getTo() : relation.getFrom();
                if (entityId.getEntityType() == EntityType.ROLES) {
                    futures.add(findRolesByIdAsync(tenantId, new RolesId(entityId.getId())));
                }
            }
            return Futures.successfulAsList(futures);
        }, MoreExecutors.directExecutor());
        role = Futures.transform(role, rolesList ->
                        rolesList == null ?
                                Collections.emptyList() :
                                rolesList.stream()
                                        .filter(roles -> query.getRolesTypes().contains(roles.getType()))
                                        .collect(Collectors.toList()),
                MoreExecutors.directExecutor()
        );
        return role;
    }

    private final PaginatedRemover<TenantId, Roles> tenantRolesRemover = new PaginatedRemover<>() {

        @Override
        protected PageData<Roles> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
            return rolesDao.findRolesByTenantId(id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, Roles roles) {
            deleteRoles(tenantId, roles);
        }
    };


    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findRolesById(tenantId, new RolesId(entityId.getId())));
    }

    @Override
    public long countByTenantId(TenantId tenantId) {
        return rolesDao.countByTenantId(tenantId);
    }

    @Override
    @Transactional
    public void deleteEntity(TenantId tenantId, EntityId id) {
        deleteRoles(tenantId, (RolesId) id);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ROLES;
    }



}
