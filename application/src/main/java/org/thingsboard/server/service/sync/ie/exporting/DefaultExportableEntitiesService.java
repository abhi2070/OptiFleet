
package org.thingsboard.server.service.sync.ie.exporting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.entity.EntityDaoService;
import org.thingsboard.server.dao.entity.EntityServiceRegistry;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.permission.AccessControlService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@TbCoreComponent
@RequiredArgsConstructor
@Slf4j
public class DefaultExportableEntitiesService implements ExportableEntitiesService {

    private final Map<EntityType, Dao<?>> daos = new HashMap<>();

    private final EntityServiceRegistry entityServiceRegistry;
    private final AccessControlService accessControlService;

    @Override
    public <E extends ExportableEntity<I>, I extends EntityId> E findEntityByTenantIdAndExternalId(TenantId tenantId, I externalId) {
        EntityType entityType = externalId.getEntityType();
        Dao<E> dao = getDao(entityType);

        E entity = null;

        if (dao instanceof ExportableEntityDao) {
            ExportableEntityDao<I, E> exportableEntityDao = (ExportableEntityDao<I, E>) dao;
            entity = exportableEntityDao.findByTenantIdAndExternalId(tenantId.getId(), externalId.getId());
        }
        if (entity == null || !belongsToTenant(entity, tenantId)) {
            return null;
        }

        return entity;
    }

    @Override
    public <E extends HasId<I>, I extends EntityId> E findEntityByTenantIdAndId(TenantId tenantId, I id) {
        E entity = findEntityById(id);

        if (entity == null || !belongsToTenant(entity, tenantId)) {
            return null;
        }
        return entity;
    }

    @Override
    public <E extends HasId<I>, I extends EntityId> E findEntityById(I id) {
        EntityType entityType = id.getEntityType();
        Dao<E> dao = getDao(entityType);
        if (dao == null) {
            throw new IllegalArgumentException("Unsupported entity type " + entityType);
        }
        return dao.findById(TenantId.SYS_TENANT_ID, id.getId());
    }

    @Override
    public <E extends ExportableEntity<I>, I extends EntityId> E findEntityByTenantIdAndName(TenantId tenantId, EntityType entityType, String name) {
        Dao<E> dao = getDao(entityType);

        E entity = null;

        if (dao instanceof ExportableEntityDao) {
            ExportableEntityDao<I, E> exportableEntityDao = (ExportableEntityDao<I, E>) dao;
            try {
                entity = exportableEntityDao.findByTenantIdAndName(tenantId.getId(), name);
            } catch (UnsupportedOperationException ignored) {
            }
        }
        if (entity == null || !belongsToTenant(entity, tenantId)) {
            return null;
        }

        return entity;
    }

    @Override
    public <E extends ExportableEntity<I>, I extends EntityId> PageData<E> findEntitiesByTenantId(TenantId tenantId, EntityType entityType, PageLink pageLink) {
        ExportableEntityDao<I, E> dao = getExportableEntityDao(entityType);
        if (dao != null) {
            return dao.findByTenantId(tenantId.getId(), pageLink);
        } else {
            return new PageData<>();
        }
    }

    @Override
    public <I extends EntityId> PageData<I> findEntitiesIdsByTenantId(TenantId tenantId, EntityType entityType, PageLink pageLink) {
        ExportableEntityDao<I, ?> dao = getExportableEntityDao(entityType);
        if (dao != null) {
            return dao.findIdsByTenantId(tenantId.getId(), pageLink);
        } else {
            return new PageData<>();
        }
    }

    @Override
    public <I extends EntityId> I getExternalIdByInternal(I internalId) {
        ExportableEntityDao<I, ?> dao = getExportableEntityDao(internalId.getEntityType());
        if (dao != null) {
            return dao.getExternalIdByInternal(internalId);
        } else {
            return null;
        }
    }

    private boolean belongsToTenant(HasId<? extends EntityId> entity, TenantId tenantId) {
        return tenantId.equals(((HasTenantId) entity).getTenantId());
    }


    @Override
    public <I extends EntityId> void removeById(TenantId tenantId, I id) {
        EntityType entityType = id.getEntityType();
        EntityDaoService entityService = entityServiceRegistry.getServiceByEntityType(entityType);
        if (entityService == null) {
            throw new IllegalArgumentException("Unsupported entity type " + entityType);
        }
        entityService.deleteEntity(tenantId, id);
    }

    private <I extends EntityId, E extends ExportableEntity<I>> ExportableEntityDao<I, E> getExportableEntityDao(EntityType entityType) {
        Dao<E> dao = getDao(entityType);
        if (dao instanceof ExportableEntityDao) {
            return (ExportableEntityDao<I, E>) dao;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <E> Dao<E> getDao(EntityType entityType) {
        return (Dao<E>) daos.get(entityType);
    }

    @Autowired
    private void setDaos(Collection<Dao<?>> daos) {
        daos.forEach(dao -> {
            if (dao.getEntityType() != null) {
                this.daos.put(dao.getEntityType(), dao);
            }
        });
    }

}
