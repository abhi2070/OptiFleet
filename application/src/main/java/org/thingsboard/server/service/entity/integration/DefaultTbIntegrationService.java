package org.thingsboard.server.service.entity.integration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.dao.integration.IntegrationService;
import org.thingsboard.server.service.entity.AbstractTbEntityService;

import static org.thingsboard.server.dao.integration.BaseIntegrationService.TB_SERVICE_QUEUE;

@Service
@AllArgsConstructor
public class DefaultTbIntegrationService extends AbstractTbEntityService implements  TbIntegrationService{

    private IntegrationService integrationServices;

    @Override
    @Transactional
    public Integration save(Integration integration, User user) throws Exception {
        ActionType actionType = integration.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = integration.getTenantId();
        try {
            if (TB_SERVICE_QUEUE.equals(integration.getType())) {
                throw new ThingsboardException("Unable to save integration with type " + TB_SERVICE_QUEUE, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            Integration savedIntegration = checkNotNull(integrationServices.saveIntegration(integration));
            autoCommit(user, savedIntegration.getId());

            return savedIntegration;
        }
        catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(org.thingsboard.server.common.data.EntityType.INTEGRATION), integration, actionType, user, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Integration integration, User user) {
        ActionType actionType = ActionType.DELETED;
        TenantId tenantId = integration.getTenantId();
        IntegrationId integrationId = integration.getId();

        try {
            removeAlarmsByEntityId(tenantId, integrationId);
            integrationServices.deleteIntegration(tenantId, integrationId);
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(org.thingsboard.server.common.data.EntityType.INTEGRATION), actionType, user, e,
                    integrationId.toString());
            throw e;
        }
    }

    @Override
    public Integration assignIntegrationToCustomer(TenantId tenantId, IntegrationId integrationId, Customer customer, User user) throws ThingsboardException {
        return null;
    }

    @Override
    public Integration unassignIntegrationToCustomer(TenantId tenantId, IntegrationId integrationId, Customer customer, User user) throws ThingsboardException {
        return null;
    }

    @Override
    public Integration assignIntegrationToPublicCustomer(TenantId tenantId, IntegrationId integrationId, User user) throws ThingsboardException {
        return null;
    }

    @Override
    public Integration assignIntegrationToEdge(TenantId tenantId, IntegrationId integrationId, Edge edge, User user) throws ThingsboardException {
        return null;
    }

    @Override
    public Integration unassignIntegrationFromEdge(TenantId tenantId, Integration integration, Edge edge, User user) throws ThingsboardException {
        return null;
    }
}
