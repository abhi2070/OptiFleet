package org.thingsboard.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantService;

@Component
public class DriverDataValidator extends DataValidator<Driver> {

    @Autowired
    private TenantService tenantService;

    @Override
    protected void validateCreate(TenantId tenantId, Driver driver) {
        validateNumberOfEntitiesPerTenant(tenantId, EntityType.DRIVER);
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Driver driver) {
        validateString("Driver name", driver.getName());
        if (driver.getTenantId() == null) {
            throw new DataValidationException("Driver should be assigned to tenant!");
        } else {
            if (!tenantService.tenantExists(driver.getTenantId())) {
                throw new DataValidationException("Driver is referencing to non-existent tenant!");
            }
        }
    }

}
