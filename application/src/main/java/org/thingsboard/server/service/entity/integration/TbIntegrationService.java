package org.thingsboard.server.service.entity.integration;

import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;

public interface TbIntegrationService {

    Integration save(Integration integration, User user) throws Exception;

    void delete(Integration integration, User user);

    Integration assignIntegrationToCustomer(TenantId tenantId, IntegrationId integrationId, Customer customer, User user) throws ThingsboardException;

    Integration unassignIntegrationToCustomer(TenantId tenantId, IntegrationId integrationId, Customer customer, User user) throws ThingsboardException;

    Integration assignIntegrationToPublicCustomer(TenantId tenantId, IntegrationId integrationId, User user) throws ThingsboardException;

    Integration assignIntegrationToEdge(TenantId tenantId, IntegrationId integrationId, Edge edge, User user) throws ThingsboardException;

    Integration unassignIntegrationFromEdge(TenantId tenantId, Integration integration, Edge edge, User user) throws ThingsboardException;
}
