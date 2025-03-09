package org.thingsboard.server.service.entity.vehicle;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.VehicleId;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.VehicleEntity;
import org.thingsboard.server.dao.sql.vehicle.VehicleRepository;
import org.thingsboard.server.dao.vehicle.VehicleService;
import org.thingsboard.server.service.entity.AbstractTbEntityService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DefaultTbVehicleService extends AbstractTbEntityService implements TbVehicleService {

    private final VehicleService vehicleService;
    private final VehicleRepository vehicleRepository;
    public static final String DUPLICATE_MESSAGE = "such vehicle_number already exists!";

    @Override
    @Transactional
    public Vehicle save(Vehicle vehicle, User user) throws Exception {
        ActionType actionType = vehicle.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        if (actionType == ActionType.UPDATED) {
            Vehicle existingVehicle = DaoUtil.getData(vehicleRepository.findVehicleInfoById(vehicle.getId().getId()));
            if (vehicle.getVehiclenumber() != null) {
                VehicleEntity vehicleEntity = vehicleRepository.findByVehiclenumber(vehicle.getVehiclenumber());
                if (!vehicleEntity.getId().toString().equals(existingVehicle.getId().toString())) {
                    throw new ThingsboardException("Vehicle with " + DUPLICATE_MESSAGE, ThingsboardErrorCode.DUPLICATE_ENTRY);
                }
            }
            updateVehicleDetails(existingVehicle, vehicle);
        } else {
            VehicleEntity vehicleEntity = vehicleRepository.findByVehiclenumber(vehicle.getVehiclenumber());
            if (vehicleEntity != null && (vehicle.getId() == null || !vehicleEntity.getId().equals(vehicle.getId()))) {
                throw new ThingsboardException("Vehicle with the given vehicle number already exists.", ThingsboardErrorCode.DUPLICATE_ENTRY);
            }
        }
        return saveOrUpdateVehicle(vehicle, actionType, user);
    }

    @Override
    @Transactional
    public void delete(Vehicle vehicle, User user) {
        ActionType actionType = ActionType.DELETED;
        TenantId tenantId = vehicle.getTenantId();
        VehicleId vehicleId = vehicle.getId();
        try {
            vehicleService.deleteVehicle(tenantId, vehicleId);
            notificationEntityService.logEntityAction(tenantId, vehicleId, vehicle, actionType, user, vehicleId.toString());
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.VEHICLE), actionType, user, e, vehicleId.toString());
            throw e;
        }
    }


    @Override
    @Transactional
    public Vehicle updateDocument(Vehicle vehicle, User user, MultipartFile registrationCertificate, MultipartFile insuranceCertificate, MultipartFile pucCertificate, MultipartFile requiredPermits) throws Exception {
        ActionType actionType = ActionType.UPDATED;
        TenantId tenantId = vehicle.getTenantId();
        if (vehicle.getId() == null) {
            throw new ThingsboardException("Vehicle ID cannot be null for update", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
        Vehicle existingVehicle = vehicleService.findVehicleById(tenantId, vehicle.getId());
        if (existingVehicle == null) {
            throw new ThingsboardException("Vehicle with ID " + vehicle.getId() + " not found", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        if (registrationCertificate != null && !registrationCertificate.isEmpty()) {
            existingVehicle.setRegistrationCertificate(registrationCertificate.getBytes());
        }
        if (insuranceCertificate != null && !insuranceCertificate.isEmpty()) {
            existingVehicle.setInsuranceCertificate(insuranceCertificate.getBytes());
        }
        if (pucCertificate != null && !pucCertificate.isEmpty()) {
            existingVehicle.setPucCertificate(pucCertificate.getBytes());
        }
        if (requiredPermits != null && !requiredPermits.isEmpty()) {
            existingVehicle.setRequiredPermits(requiredPermits.getBytes());
        }
        return saveOrUpdateVehicle(existingVehicle, actionType, user);
    }

    @Override
    public boolean isVerifiedVehicle(Vehicle vehicle, User user) throws Exception {
        if (vehicle == null) {
            return false;
        }
        boolean isVerified = vehicle.getTenantId() != null && vehicle.getVehiclenumber() != null && !vehicle.getVehiclenumber().trim().isEmpty() && vehicle.getType() != null && !vehicle.getType().trim().isEmpty() && vehicle.getRegistrationCertificate() != null && vehicle.getInsuranceCertificate() != null && vehicle.getPucCertificate() != null && vehicle.getRequiredPermits() != null;
        vehicle.setStatus(isVerified ? "AVAILABLE" : "PENDING");
        saveOrUpdateVehicle(vehicle, ActionType.UPDATED, user);
        return isVerified;
    }

    private Vehicle saveOrUpdateVehicle(Vehicle vehicle, ActionType actionType, User user) throws Exception {
        TenantId tenantId = vehicle.getTenantId();
        try {
            Vehicle savedVehicle = checkNotNull(vehicleService.saveVehicle(vehicle));
            autoCommit(user, savedVehicle.getId());
            notificationEntityService.logEntityAction(tenantId, savedVehicle.getId(), savedVehicle, actionType, user);
            return savedVehicle;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.VEHICLE), vehicle, ActionType.UPDATED, user, e);
            throw e;
        }
    }

    private static void updateVehicleDetails(Vehicle existingVehicle, Vehicle vehicle) {
        if (vehicle.getVehiclenumber() != null) {
            if (existingVehicle.getVehiclenumber().equals(vehicle.getVehiclenumber())) {
                vehicle.setVehiclenumber(existingVehicle.getVehiclenumber());
            } else {
                vehicle.setVehiclenumber(vehicle.getVehiclenumber());
            }
        } else {
            vehicle.setVehiclenumber(existingVehicle.getVehiclenumber());
        }
        if (vehicle.getType() == null) vehicle.setType(existingVehicle.getType());
        if (vehicle.getNextService() == null) vehicle.setNextService(existingVehicle.getNextService());
        if (vehicle.getStatus() == null) vehicle.setStatus(existingVehicle.getStatus());
        if (vehicle.getDevice() == null || vehicle.getDevice().isEmpty()) {
            vehicle.setDevice(existingVehicle.getDevice());
        } else {
            List<UUID> devices = vehicle.getDevice().stream().
                    filter(Objects::nonNull).
                    distinct().collect(Collectors.toList());
            vehicle.setDevice(devices);
        }
    }
}
