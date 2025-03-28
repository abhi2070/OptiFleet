
package org.thingsboard.server.dao.sql.entityview;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.EntityViewInfo;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.entityview.EntityViewDao;
import org.thingsboard.server.dao.model.sql.EntityViewEntity;
import org.thingsboard.server.dao.model.sql.EntityViewInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.thingsboard.server.dao.DaoUtil.convertTenantEntityTypesToDto;

/**
 * Created by Victor Basanets on 8/31/2017.
 */
@Component
@Slf4j
@SqlDao
public class JpaEntityViewDao extends JpaAbstractDao<EntityViewEntity, EntityView>
        implements EntityViewDao {

    @Autowired
    private EntityViewRepository entityViewRepository;

    @Override
    protected Class<EntityViewEntity> getEntityClass() {
        return EntityViewEntity.class;
    }

    @Override
    protected JpaRepository<EntityViewEntity, UUID> getRepository() {
        return entityViewRepository;
    }

    @Override
    public EntityViewInfo findEntityViewInfoById(TenantId tenantId, UUID entityViewId) {
        return DaoUtil.getData(entityViewRepository.findEntityViewInfoById(entityViewId));
    }

    @Override
    public PageData<EntityView> findEntityViewsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findEntityViewInfosByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EntityViewInfoEntity.entityViewInfoColumnMap)));
    }

    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findEntityViewInfosByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EntityViewInfoEntity.entityViewInfoColumnMap)));
    }

    @Override
    public Optional<EntityView> findEntityViewByTenantIdAndName(UUID tenantId, String name) {
        return Optional.ofNullable(
                DaoUtil.getData(entityViewRepository.findByTenantIdAndName(tenantId, name)));
    }

    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndCustomerId(UUID tenantId,
                                                                       UUID customerId,
                                                                       PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)
                ));
    }

    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findEntityViewInfosByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EntityViewInfoEntity.entityViewInfoColumnMap)));
    }

    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)
                ));
    }

    @Override
    public PageData<EntityViewInfo> findEntityViewInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                entityViewRepository.findEntityViewInfosByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, EntityViewInfoEntity.entityViewInfoColumnMap)));
    }

    @Override
    public List<EntityView> findEntityViewsByTenantIdAndEntityId(UUID tenantId, UUID entityId) {
        return DaoUtil.convertDataList(
                entityViewRepository.findAllByTenantIdAndEntityId(tenantId, entityId));
    }

    @Override
    public boolean existsByTenantIdAndEntityId(UUID tenantId, UUID entityId) {
        return entityViewRepository.existsByTenantIdAndEntityId(tenantId, entityId);
    }

    @Override
    public ListenableFuture<List<EntitySubtype>> findTenantEntityViewTypesAsync(UUID tenantId) {
        return service.submit(() -> convertTenantEntityTypesToDto(tenantId, EntityType.ENTITY_VIEW, entityViewRepository.findTenantEntityViewTypes(tenantId)));
    }

    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink) {
        log.debug("Try to find entity views by tenantId [{}], edgeId [{}] and pageLink [{}]", tenantId, edgeId, pageLink);
        return DaoUtil.toPageData(entityViewRepository
                .findByTenantIdAndEdgeId(
                        tenantId,
                        edgeId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<EntityView> findEntityViewsByTenantIdAndEdgeIdAndType(UUID tenantId, UUID edgeId, String type, PageLink pageLink) {
        log.debug("Try to find entity views by tenantId [{}], edgeId [{}], type [{}] and pageLink [{}]", tenantId, edgeId, type, pageLink);
        return DaoUtil.toPageData(entityViewRepository
                .findByTenantIdAndEdgeIdAndType(
                        tenantId,
                        edgeId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public EntityView findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
//        return DaoUtil.getData(entityViewRepository.findByTenantIdAndExternalId(tenantId, externalId));
        return null;
    }

    @Override
    public PageData<EntityView> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findEntityViewsByTenantId(tenantId, pageLink);
    }

    @Override
    public EntityViewId getExternalIdByInternal(EntityViewId internalId) {
        return Optional.ofNullable(entityViewRepository.getExternalIdById(internalId.getId()))
                .map(EntityViewId::new).orElse(null);
    }

    @Override
    public EntityView findByTenantIdAndName(UUID tenantId, String name) {
        return findEntityViewByTenantIdAndName(tenantId, name).orElse(null);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ENTITY_VIEW;
    }
}
