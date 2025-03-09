package org.thingsboard.server.dao.integration;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.integration.IntegrationInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IntegrationDao extends Dao<Integration>, TenantEntityDao, ExportableEntityDao<IntegrationId, Integration> {

    IntegrationInfo findIntegrationInfoById(TenantId tenantId, UUID integrationId);

    Integration save(TenantId tenantId, Integration integration);

    PageData<Integration> findIntegrationsByTenantId(UUID tenantId, PageLink pageLink);

    PageData<Integration> findIntegrationsByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);

    PageData<IntegrationInfo> findIntegrationInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);

    PageData<IntegrationInfo> findIntegrationInfosByTenantId(UUID tenantId, PageLink pageLink);

    PageData<Integration> findIntegrations(int pageSize, int page, String sortProperty, SortOrder sortOrder, String type);

    ListenableFuture<List<Integration>> findIntegrationsByTenantIdAndIdsAsync(UUID tenantId, List<UUID> integrationIds);

    PageData<Integration> findIntegrationsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);

    PageData<Integration> findIntegrationsByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink);

    ListenableFuture<List<Integration>> findIntegrationsByTenantIdAndCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> integrationIds);

    Optional<Integration> findIntegrationsByTenantIdAndName(UUID tenantId, String name);

    PageData<Integration> findIntegrationsByTenantIdAndEdgeId(UUID tenantId, UUID edgeId, PageLink pageLink);

    PageData<Integration> findIntegrationsByTenantIdAndEdgeIdAndType(UUID tenantId, UUID edgeId, String type, PageLink pageLink);

}
