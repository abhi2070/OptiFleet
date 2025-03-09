package org.thingsboard.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.dao.vehicle.BaseVehicleService;
import org.thingsboard.server.dao.vehicle.VehicleDao;

@Component
public class VehicleDataValidator extends DataValidator<Vehicle> {

    @Autowired
    private VehicleDao vehicleDao;

    @Autowired
    @Lazy
    private TenantService tenantService;



    @Override
    protected void validateCreate(TenantId tenantId, Vehicle vehicle) {
        if (!BaseVehicleService.TB_SERVICE_QUEUE.equals(vehicle.getType())) {
            validateNumberOfEntitiesPerTenant(tenantId, EntityType.VEHICLE);
        }
    }

    @Override
    protected Vehicle validateUpdate(TenantId tenantId, Vehicle vehicle) {
        Vehicle old = vehicleDao.findById(vehicle.getTenantId(), vehicle.getId().getId());
        if (old == null) {
            throw new DataValidationException("Can't update non existing vehicle!");
        }
        return old;
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Vehicle vehicle) {
        validateString("Vehicle name", vehicle.getName());
        if (vehicle.getTenantId() == null) {
            throw new DataValidationException("Vehicle should be assigned to tenant!");
        } else {
            if (!tenantService.tenantExists(vehicle.getTenantId())) {
                throw new DataValidationException("Vehicle is referencing to non-existent tenant!");
            }
        }

    }
}
