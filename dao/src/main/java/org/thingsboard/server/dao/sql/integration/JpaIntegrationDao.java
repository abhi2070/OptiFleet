package org.thingsboard.server.dao.sql.integration;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.integration.IntegrationInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.integration.IntegrationDao;
import org.thingsboard.server.dao.model.sql.IntegrationEntity;
import org.thingsboard.server.dao.model.sql.IntegrationInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.thingsboard.server.dao.integration.BaseIntegrationService.TB_SERVICE_QUEUE;

@Component
@SqlDao
@Slf4j
public class JpaIntegrationDao extends JpaAbstractDao<IntegrationEntity, Integration> implements IntegrationDao {

    @Autowired
    private IntegrationRepository integrationRepository;

    @Override
    protected Class<IntegrationEntity> getEntityClass() {
        return IntegrationEntity.class;
    }

    @Override
    protected JpaRepository<IntegrationEntity, UUID> getRepository() {
        return integrationRepository;
    }

    @Override
    public IntegrationInfo findIntegrationInfoById(TenantId tenantId, UUID integrationId) {
        return DaoUtil.getData(integrationRepository.findIntegrationInfoById(integrationId));
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(integrationRepository
                .findByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public ListenableFuture<List<Integration>> findIntegrationsByTenantIdAndIdsAsync(UUID tenantId, List<UUID> integrationIds) {
        return service.submit(() ->
                DaoUtil.convertDataList(integrationRepository.findByTenantIdAndIdIn(tenantId, integrationIds)));
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(integrationRepository
                .findByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<IntegrationInfo> findIntegrationInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                integrationRepository.findIntegrationInfosByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, IntegrationInfoEntity.integrationInfoColumnMap)));
    }

    @Override
    public PageData<IntegrationInfo> findIntegrationInfosByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                integrationRepository.findIntegrationInfosByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, IntegrationInfoEntity.integrationInfoColumnMap)));
    }

    @Override
    public PageData<Integration> findIntegrations(int pageSize, int page, String sortProperty, SortOrder sortOrder, String type) {
        PageLink pageLink = new PageLink(pageSize, page, sortProperty, sortOrder);
        return DaoUtil.toPageData(
                integrationRepository.findIntegrations(
                        type,
                        DaoUtil.toPageable(pageLink)
                )
        );
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink) {
        return DaoUtil.toPageData(integrationRepository
                .findByTenantIdAndCustomerId(
                        tenantId,
                        customerId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(integrationRepository
                .findByTenantIdAndCustomerIdAndType(
                        tenantId,
                        customerId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public ListenableFuture<List<Integration>> findIntegrationsByTenantIdAndCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> integrationIds) {
        return service.submit(() ->
                DaoUtil.convertDataList(integrationRepository.findByTenantIdAndCustomerIdAndIdIn(tenantId, customerId, integrationIds)));
    }

    @Override
    public Optional<Integration> findIntegrationsByTenantIdAndName(UUID tenantId, String name) {
        Integration integration = DaoUtil.getData(integrationRepository.findByTenantIdAndName(tenantId, name));
        return Optional.ofNullable(integration);
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink) {
        log.debug("Try to find integrations by tenantId [{}], edgeId [{}] and pageLink [{}]", tenantId, edgeId, pageLink);
        return DaoUtil.toPageData(integrationRepository
                .findByTenantIdAndEdgeId(
                        tenantId,
                        edgeId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<Integration> findIntegrationsByTenantIdAndEdgeIdAndType(UUID tenantId, UUID edgeId, String type, PageLink pageLink) {
        log.debug("Try to find integrations by tenantId [{}], edgeId [{}], type [{}] and pageLink [{}]", tenantId, edgeId, type, pageLink);
        return DaoUtil.toPageData(integrationRepository
                .findByTenantIdAndEdgeIdAndType(
                        tenantId,
                        edgeId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public Integration findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
//        return DaoUtil.getData(integrationRepository.findByTenantIdAndExternalId(tenantId, externalId));
        return null;
    }

    @Override
    public PageData<Integration> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findIntegrationsByTenantId(tenantId, pageLink);
    }

    @Override
    public IntegrationId getExternalIdByInternal(IntegrationId internalId) {
        return Optional.ofNullable(integrationRepository.getExternalIdById(internalId.getId()))
                .map(IntegrationId::new).orElse(null);
    }

    @Override
    public Long countByTenantId(TenantId tenantId) {
        return integrationRepository.countByTenantIdAndTypeIsNot(tenantId.getId(), TB_SERVICE_QUEUE);
    }

    @Override
    public Integration findByTenantIdAndName(UUID tenantId, String name) {
        return findIntegrationsByTenantIdAndName(tenantId, name).orElse(null);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.INTEGRATION;
    }
}
