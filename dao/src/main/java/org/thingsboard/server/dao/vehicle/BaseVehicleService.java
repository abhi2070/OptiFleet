package org.thingsboard.server.dao.vehicle;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.dao.entity.AbstractCachedEntityService;
import org.thingsboard.server.dao.entity.EntityCountService;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.thingsboard.server.dao.service.Validator.*;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;

@Service("VehichleDaoService")
@Slf4j
public class BaseVehicleService extends AbstractCachedEntityService<VehicleCacheKey, Vehicle, VehicleCacheEvictEvent> implements VehicleService {

    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_VEHICLE_ID = "Incorrect vehicleId ";
    public static final String TB_SERVICE_QUEUE = "TbServiceQueue";
    public static final String DUPLICATE_MESSAGE = "such vehicle_number already exists!";

    @Autowired
    private VehicleDao vehicleDao;

    @Autowired
    private DataValidator<Vehicle> VehicleValidator;

    @Autowired
    private EntityCountService countService;

    @TransactionalEventListener(classes = VehicleCacheEvictEvent.class)
    @Override
    public void handleEvictEvent(VehicleCacheEvictEvent event) {
        List<VehicleCacheKey> keys = new ArrayList<>(2);
        keys.add(new VehicleCacheKey(event.getTenantId(), event.getNewNumber()));
        if (StringUtils.isNotEmpty(event.getOldNumber()) && !event.getOldNumber().equals(event.getNewNumber())) {
            keys.add(new VehicleCacheKey(event.getTenantId(), event.getOldNumber()));
        }
        cache.evict(keys);
    }

    @Override
    public Vehicle findVehicleById(TenantId tenantId, VehicleId vehicleId) {
        validateId(vehicleId, INCORRECT_VEHICLE_ID + vehicleId);
        return vehicleDao.findById(tenantId, vehicleId.getId());
    }

    @Override
    public Vehicle saveVehicle(Vehicle vehicle) {
        return doSaveVehicle(vehicle, true);
    }

    private Vehicle doSaveVehicle(Vehicle vehicle, boolean doValidate)  {
        if (doValidate) {
            VehicleValidator.validate(vehicle, Vehicle::getTenantId);
        }
        try {
            Vehicle saved = vehicleDao.save(vehicle.getTenantId(), vehicle);
            return saved;
        } catch (Exception e) {
            checkConstraintViolation(e,
                    "Vehicle_number_unq_key", "Vehicle with such Vehicle_number already exists!");
            throw e;
        }
    }

    @Override
    public void deleteVehicle(TenantId tenantId, VehicleId vehicleId) {
        validateId(vehicleId, INCORRECT_VEHICLE_ID + vehicleId);
        if (entityViewService.existsByTenantIdAndEntityId(tenantId, vehicleId)) {
            throw new DataValidationException("Can't delete vehicle that has entity views!");
        }

        Vehicle vehicle = vehicleDao.findById(tenantId, vehicleId.getId());
        alarmService.deleteEntityAlarmRelations(tenantId, vehicleId);
        deleteVehicle(tenantId, vehicle);
    }
    private void deleteVehicle(TenantId tenantId, Vehicle vehicle) {
        relationService.deleteEntityRelations(tenantId, vehicle.getId());
        vehicleDao.removeById(tenantId, vehicle.getUuidId());

        publishEvictEvent(new VehicleCacheEvictEvent(vehicle.getTenantId(), vehicle.getVehiclenumber(), null));
        countService.publishCountEntityEvictEvent(tenantId, EntityType.VEHICLE);
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(vehicle.getId()).build());
    }

    @Override
    public PageData<Vehicle> findVehicleByTenantId(TenantId tenantId, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return vehicleDao.findVehicleByTenantId(tenantId.getId(), pageLink);
    }

    private final PaginatedRemover<TenantId, Vehicle> tenantVehicleRemover = new PaginatedRemover<>() {

        @Override
        protected PageData<Vehicle> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
            return vehicleDao.findVehicleByTenantId(id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, Vehicle vehicle) {
            deleteVehicle(tenantId, vehicle);
        }
    };


    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findVehicleById(tenantId, new VehicleId(entityId.getId())));
    }

    @Override
    public long countByTenantId(TenantId tenantId) {
        return vehicleDao.countByTenantId(tenantId);
    }

    @Override
    @Transactional
    public void deleteEntity(TenantId tenantId, EntityId id) {
        deleteVehicle(tenantId, (VehicleId) id);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.VEHICLE;
    }

    @Override
    public PageData<Vehicle> findVehicleByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return vehicleDao.findVehicleByTenantIdAndType(tenantId.getId(), type, pageLink);
    }


}
