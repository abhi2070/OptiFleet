package org.thingsboard.server.dao.integration;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.integration.IntegrationInfo;
import org.thingsboard.server.common.data.integration.IntegrationSearchQuery;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

public interface IntegrationService extends EntityDaoService {

    IntegrationInfo findIntegrationInfoById(TenantId tenantId, IntegrationId integrationId);

    Integration findIntegrationById(TenantId tenantId, IntegrationId integrationId);

    ListenableFuture<Integration> findIntegrationByIdAsync(TenantId tenantId, IntegrationId integrationId);

    Integration findIntegrationByTenantIdAndName(TenantId tenantId, String name);

    Integration saveIntegration(Integration integration, boolean doValidate);

    Integration saveIntegration(Integration integration);

    Integration assignIntegrationToCustomer(TenantId tenantId, IntegrationId integrationId, CustomerId customerId);

    Integration unassignIntegrationFromCustomer(TenantId tenantId, IntegrationId integrationId);

    void deleteIntegration(TenantId tenantId, IntegrationId integrationId);

    PageData<Integration> findIntegrationsByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<Integration> findIntegrationsByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    PageData<IntegrationInfo> findIntegrationInfosByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    PageData<IntegrationInfo> findIntegrationInfosByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<Integration> findIntegrations(int pageSize, int page, String sortProperty, SortOrder sortOrder, String type);

    ListenableFuture<List<Integration>> findIntegrationsByTenantIdAndIdsAsync(TenantId tenantId, List<IntegrationId> integrationIds);

    PageData<Integration> findIntegrationsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    PageData<Integration> findIntegrationsByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    ListenableFuture<List<Integration>> findIntegrationsByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<IntegrationId> integrationIds);

    ListenableFuture<List<Integration>> findIntegrationsByQuery(TenantId tenantId, IntegrationSearchQuery query);

    Integration assignIntegrationToEdge(TenantId tenantId, IntegrationId integrationId, EdgeId edgeId);

    Integration unassignIntegrationFromEdge(TenantId tenantId, IntegrationId integrationId, EdgeId edgeId);

    PageData<Integration> findIntegrationsByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink);

    PageData<Integration> findIntegrationsByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink);
}
