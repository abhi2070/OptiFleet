package org.thingsboard.server.service.entity.trip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Trip;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DriverId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TripId;
import org.thingsboard.server.common.data.id.VehicleId;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.driver.DriverService;
import org.thingsboard.server.dao.model.sql.DriverEntity;
import org.thingsboard.server.dao.model.sql.TripEntity;
import org.thingsboard.server.dao.model.sql.VehicleEntity;
import org.thingsboard.server.dao.sql.driver.DriverRepository;
import org.thingsboard.server.dao.sql.trip.TripRepository;
import org.thingsboard.server.dao.sql.vehicle.VehicleRepository;
import org.thingsboard.server.dao.trip.TripService;
import org.thingsboard.server.dao.vehicle.VehicleService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.AbstractTbEntityService;
import org.thingsboard.server.service.entity.driver.TbDriverService;
import org.thingsboard.server.service.entity.vehicle.TbVehicleService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@TbCoreComponent
@RequiredArgsConstructor
@Slf4j
public class DefaultTbTripService extends AbstractTbEntityService implements TbTripService {

    private final TripService tripService;
    private final TbVehicleService tbVehicleService;
    private final TbDriverService tbDriverService;
    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public static final String DUPLICATE_MESSAGE = "such name already exists!";
    public static final String INVALID_UUID_MESSAGE = "invalid uuid cannot save!";
    public static final String NOT_AVAILABLE = " not available!";
    public static final String INVALID_DATA = " assigned to the trip!";
    public static final String INVALID_STATUS = "cannot start because it has not been scheduled.";

    @Override
    @Transactional
    public Trip save(Trip trip, User user) throws Exception {
        ActionType actionType = trip.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        if (actionType == ActionType.UPDATED) {
            Trip existingTrip = DaoUtil.getData(tripRepository.findTripInfoById(trip.getId().getId()));
            if (trip.getName() != null) {
                TripEntity tripEntity = tripRepository.findByTripName(trip.getName());
                if (!tripEntity.getId().toString().equals(existingTrip.getId().toString())) {
                    throw new ThingsboardException("Trip with " + DUPLICATE_MESSAGE, ThingsboardErrorCode.DUPLICATE_ENTRY);
                }
            }
            updateTripDetails(existingTrip, trip);
            return saveOrUpdateTrip(trip, actionType, user);
        }
        validateTrip(trip);
        Vehicle vehicle = validateVehicle(trip.getAssignedVehicle(), actionType);
        List<Driver> drivers = validateDrivers(trip.getAssignedDriver(), actionType);
        if (vehicle != null && drivers != null) {
            tbVehicleService.save(vehicle, user);
            for (Driver driver : drivers) {
                tbDriverService.save(driver, user);
            }
        }
        trip.setStatus("SCHEDULED");
        return saveOrUpdateTrip(trip, actionType, user);
    }

    @Override
    public void delete(Trip entity, User user) {

    }

    @Override
    public Trip startOrStopTrip(Trip existingTrip, Trip trip, User user) throws Exception {
        ActionType actionType = ActionType.UPDATED;
        if (existingTrip.getStatus() == null) {
            throw new ThingsboardException("The trip " + INVALID_STATUS, ThingsboardErrorCode.INVALID_DATA);
        }
        trip.setStatus(existingTrip.getStatus().equalsIgnoreCase("START") ? "STOP" : "START");
        updateTripDetails(existingTrip, trip);
        return saveOrUpdateTrip(trip, actionType, user);
    }

    @Override
    public void deleteTrip(Trip trip, User user) throws Exception {
        ActionType actionType = ActionType.DELETED;
        TripId tripId = trip.getId();
        TenantId tenantId = trip.getTenantId();
        Vehicle vehicle = validateVehicle(trip.getAssignedVehicle(), actionType);
        List<Driver> drivers = validateDrivers(trip.getAssignedDriver(), actionType);
        if (vehicle != null && drivers != null) {
            tbVehicleService.save(vehicle, user);
            for (Driver driver : drivers) {
                tbDriverService.save(driver, user);
            }
            try {
                tripService.deleteTrip(tenantId, tripId);
            } catch (Exception e) {
                throw e;
            }
        }
    }

    private Trip saveOrUpdateTrip(Trip trip, ActionType actionType, User user) throws Exception {
        TenantId tenantId = trip.getTenantId();
        try {
            Trip savedTrip = checkNotNull(tripService.saveTrip(trip));
            autoCommit(user, savedTrip.getId());
            notificationEntityService.logEntityAction(tenantId, savedTrip.getId(), savedTrip, actionType, user);
            return savedTrip;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.TRIP), trip, actionType, user, e);
            throw e;
        }
    }

    private static void updateTripDetails(Trip existingTrip, Trip trip) {
        if (trip.getName() != null) {
            if (existingTrip.getName().equals(trip.getName())) {
                trip.setName(existingTrip.getName());
            } else {
                trip.setName(trip.getName());
            }
        } else {
            trip.setName(existingTrip.getName());
        }
        if (trip.getStatus() == null) trip.setStatus(existingTrip.getStatus());
        if (trip.getStartDate() == null) trip.setStartDate(existingTrip.getStartDate());
        if (trip.getEndDate() == null) trip.setEndDate(existingTrip.getEndDate());
        if (trip.getVehicleType() == null) trip.setVehicleType(existingTrip.getVehicleType());
        if (trip.getAssignedVehicle() == null) trip.setAssignedVehicle(existingTrip.getAssignedVehicle());
        if (trip.getAssignedDriver() == null) trip.setAssignedDriver(existingTrip.getAssignedDriver());
        if (trip.getStartLocation() == null) trip.setStartLocation(existingTrip.getStartLocation());
        if (trip.getDestination() == null) trip.setDestination(existingTrip.getDestination());
    }

    private void validateTrip(Trip trip) throws ThingsboardException {
        if (trip.getAssignedVehicle() == null) {
            throw new ThingsboardException("No vehicle " + INVALID_DATA, ThingsboardErrorCode.INVALID_DATA);
        }
        if (trip.getAssignedDriver() == null || trip.getAssignedDriver().isEmpty()) {
            throw new ThingsboardException("No driver " + INVALID_DATA, ThingsboardErrorCode.INVALID_DATA);
        }
    }

    private Vehicle validateVehicle(UUID vehicleId, ActionType actionType) throws Exception {
        Vehicle vehicleEntity = DaoUtil.getData(vehicleRepository.findVehicleInfoById(vehicleId));
        Vehicle vehicle = new Vehicle();
        vehicle.setTenantId(vehicleEntity.getTenantId());
        vehicle.setId(new VehicleId(vehicleId));
        if (vehicleEntity == null) {
            throw new ThingsboardException(
                    "Vehicle with such id: " + vehicleId + " not found",
                    ThingsboardErrorCode.ITEM_NOT_FOUND
            );
        }
        switch (actionType) {
            case ADDED:
                if (vehicleEntity.getStatus() == null || !vehicleEntity.getStatus().trim().equalsIgnoreCase("AVAILABLE")) {
                    throw new ThingsboardException(
                            "Vehicle with such id: " + vehicleId + NOT_AVAILABLE,
                            ThingsboardErrorCode.UNAVAILABLE_STATUS
                    );
                }
                vehicle.setStatus("ON_TRIP");
                break;
            case DELETED:
                if (vehicleEntity.getStatus() == null || !vehicleEntity.getStatus().trim().equalsIgnoreCase("ON_TRIP")) {
                    throw new ThingsboardException(
                            "Vehicle with such id: " + vehicleId + NOT_AVAILABLE,
                            ThingsboardErrorCode.UNAVAILABLE_STATUS);
                }
                vehicle.setStatus("AVAILABLE");
                break;
            default:
                throw new IllegalArgumentException("Unsupported action type: " + actionType);
        }
        return vehicle;
    }


    private List<Driver> validateDrivers(List<UUID> driverIds, ActionType actionType) throws Exception {
        List<Driver> availableDrivers = new ArrayList<>();
        for (UUID driverId : driverIds) {
            Driver driverEntity = DaoUtil.getData(driverRepository.findDriverInfoById(driverId));
            Driver driver = new Driver();
            driver.setTenantId(driverEntity.getTenantId());
            driver.setId(new DriverId(driverId));
            if (driverEntity == null) {
                throw new ThingsboardException(
                        "Driver with such id: " + driverId + " not found",
                        ThingsboardErrorCode.ITEM_NOT_FOUND);
            }
            switch (actionType) {
                case ADDED:
                    if (driverEntity.getStatus() == null || !driverEntity.getStatus().trim().equalsIgnoreCase("AVAILABLE")) {
                        throw new ThingsboardException(
                                "Driver with such id: " + driverId + NOT_AVAILABLE,
                                ThingsboardErrorCode.UNAVAILABLE_STATUS
                        );
                    }
                    driver.setStatus("ON_TRIP");
                    break;
                case DELETED:
                    if (driverEntity.getStatus() == null || !driverEntity.getStatus().trim().equalsIgnoreCase("ON_TRIP")) {
                        throw new ThingsboardException(
                                "Driver with such id: " + driverId + NOT_AVAILABLE,
                                ThingsboardErrorCode.UNAVAILABLE_STATUS);
                    }
                    driver.setStatus("AVAILABLE");
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported action type: " + actionType);
            }
            availableDrivers.add(driver);
        }
        return availableDrivers;
    }

}
