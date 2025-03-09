package org.thingsboard.server.dao.service.validator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.integration.BaseIntegrationService;
import org.thingsboard.server.dao.integration.IntegrationDao;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantService;

import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;

@Component
public class IntegrationDataValidator extends DataValidator<Integration> {

    @Autowired
    private IntegrationDao integrationDao;

    @Autowired
    @Lazy
    private TenantService tenantService;

    @Autowired
    private CustomerDao customerDao;

    @Override
    protected void validateCreate(TenantId tenantId, Integration integration) {
        if (!BaseIntegrationService.TB_SERVICE_QUEUE.equals(integration.getType())) {
            validateNumberOfEntitiesPerTenant(tenantId, EntityType.INTEGRATION);
        }
    }

    @Override
    protected Integration validateUpdate(TenantId tenantId, Integration integration) {
        Integration old = integrationDao.findById(integration.getTenantId(), integration.getId().getId());
        if (old == null) {
            throw new DataValidationException("Can't update non existing integration!");
        }
        return old;
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Integration integration) {
        validateString("Integration name", integration.getName());
        if (integration.getTenantId() == null) {
            throw new DataValidationException("Integration should be assigned to tenant!");
        } else {
            if (!tenantService.tenantExists(integration.getTenantId())) {
                throw new DataValidationException("Integration is referencing to non-existent tenant!");
            }
        }
        if (integration.getCustomerId() == null) {
            integration.setCustomerId(new CustomerId(NULL_UUID));
        } else if (!integration.getCustomerId().getId().equals(NULL_UUID)) {
            Customer customer = customerDao.findById(tenantId, integration.getCustomerId().getId());
            if (customer == null) {
                throw new DataValidationException("Can't assign integration to non-existent customer!");
            }
            if (!customer.getTenantId().equals(integration.getTenantId())) {
                throw new DataValidationException("Can't assign integration to customer from different tenant!");
            }
        }
    }
}
