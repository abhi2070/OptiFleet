package org.thingsboard.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Trip;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantService;

@Component
public class TripDataValidator extends DataValidator<Trip> {

    @Autowired
    private TenantService tenantService;

    @Override
    protected void validateCreate(TenantId tenantId, Trip trip) {
        validateNumberOfEntitiesPerTenant(tenantId, EntityType.TRIP);
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Trip trip) {
        validateString("Trip name", trip.getName());
        if (trip.getTenantId() == null) throw new DataValidationException("Trip should be assigned to tenant!");
        else {
            if (!tenantService.tenantExists(trip.getTenantId()))
                throw new DataValidationException("Trip is referencing to non-existent tenant!");
        }
    }
}
