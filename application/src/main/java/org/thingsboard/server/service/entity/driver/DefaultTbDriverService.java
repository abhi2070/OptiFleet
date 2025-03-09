package org.thingsboard.server.service.entity.driver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DriverId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.driver.DriverService;
import org.thingsboard.server.dao.model.sql.DriverEntity;
import org.thingsboard.server.dao.sql.driver.DriverRepository;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.AbstractTbEntityService;

import javax.transaction.Transactional;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class DefaultTbDriverService extends AbstractTbEntityService implements TbDriverService {

    private final DriverService driverService;
    private final DriverRepository driverRepository;
    public static final String DUPLICATE_MESSAGE = "such driving_license_number already exists!";

    @Override
    @Transactional
    public Driver save(Driver driver, User user) throws Exception {
        ActionType actionType = driver.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        if (actionType == ActionType.UPDATED) {
            Driver existingDriver = DaoUtil.getData(driverRepository.findDriverInfoById(driver.getId().getId()));
            if (driver.getDrivingLicenseNumber() != null) {
                DriverEntity driverEntity = driverRepository.findByDriverLicenseNumber(driver.getDrivingLicenseNumber());
                if (!driverEntity.getId().toString().equals(existingDriver.getId().toString())) {
                    throw new ThingsboardException("Driver with " + DUPLICATE_MESSAGE, ThingsboardErrorCode.DUPLICATE_ENTRY);
                }
            }
            updateDriverDetails(existingDriver, driver);
        } else {
            DriverEntity driverEntity = driverRepository.findByDriverLicenseNumber(driver.getDrivingLicenseNumber());
            if (driverEntity != null && (driver.getId() == null || !driverEntity.getId().equals(driver.getId()))) {
                throw new ThingsboardException("Driver with " + DUPLICATE_MESSAGE, ThingsboardErrorCode.DUPLICATE_ENTRY);
            }
        }
        return saveOrUpdateDriver(driver, actionType, user);
    }

    @Override
    public void delete(Driver driver, User user) {
        ActionType actionType = ActionType.DELETED;
        DriverId driverId = driver.getId();
        TenantId tenantId = driver.getTenantId();
        try {
            driverService.deleteDriver(tenantId, driverId);
            notificationEntityService.logEntityAction(tenantId, driverId, driver, actionType, user, driverId.toString());
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DRIVER), actionType, user, e, driverId.toString());
            throw e;
        }
    }

    @Override
    public boolean isVerifiedDriver(Driver driver, User user) throws Exception {
        if (driver == null) return false;
        boolean isVerified = driver.getTenantId() != null &&
                             (driver.getName() != null && !driver.getName().trim().isEmpty()) &&
                             driver.getGender() != null &&
                             driver.getServiceStartDate() != null &&
                             driver.getDateOfBirth() != null &&
                             (driver.getDrivingLicenseNumber() != null && !driver.getDrivingLicenseNumber().trim().isEmpty()) &&
                             driver.getDrivingLicenseValidity() != null &&
                             driver.getProfilePhoto() != null &&
                             driver.getDrivingLicenseDocument() != null;
        driver.setStatus(isVerified ? "AVAILABLE" : null);
        saveOrUpdateDriver(driver, ActionType.UPDATED, user);
        return isVerified;
    }

    @Override
    public Driver uploadPhotoDocument(byte[] fileData, String contentType, Driver driver, User user) throws Exception {
        ActionType actionType = ActionType.UPDATED;
        if (contentType.startsWith("image/")) driver.setProfilePhoto(fileData);
        else if (contentType.equals("application/pdf")) driver.setDrivingLicenseDocument(fileData);
        else {
            throw new ThingsboardException("Unsupported content type: " + contentType, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
        return saveOrUpdateDriver(driver, actionType, user);
    }

    private Driver saveOrUpdateDriver(Driver driver, ActionType actionType, User user) throws Exception {
        TenantId tenantId = driver.getTenantId();
        try {
            Driver savedDriver = checkNotNull(driverService.saveDriver(driver));
            autoCommit(user, savedDriver.getId());
            notificationEntityService.logEntityAction(tenantId, savedDriver.getId(), savedDriver, actionType, user);
            return savedDriver;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DRIVER), driver, actionType, user, e);
            throw e;
        }
    }

    private static void updateDriverDetails(Driver existingDriver, Driver driver) {
        if (driver.getDrivingLicenseNumber() != null) {
            if (existingDriver.getDrivingLicenseNumber().equals(driver.getDrivingLicenseNumber())) {
                driver.setDrivingLicenseNumber(existingDriver.getDrivingLicenseNumber());
            } else {
                driver.setDrivingLicenseNumber(driver.getDrivingLicenseNumber());
            }
        } else {
            driver.setDrivingLicenseNumber(existingDriver.getDrivingLicenseNumber());
        }
        if (driver.getName() == null) driver.setName(existingDriver.getName());
        if (driver.getStatus() == null) driver.setStatus(existingDriver.getStatus());
        if (driver.getGender() == null) driver.setGender(existingDriver.getGender());
        if (driver.getServiceStartDate() == null) driver.setServiceStartDate(existingDriver.getServiceStartDate());
        if (driver.getDateOfBirth() == null) driver.setDateOfBirth(existingDriver.getDateOfBirth());
        if (driver.getDrivingLicenseValidity() == null)
            driver.setDrivingLicenseValidity(existingDriver.getDrivingLicenseValidity());
        if (driver.getProfilePhoto() == null) driver.setProfilePhoto(existingDriver.getProfilePhoto());
        if (driver.getDrivingLicenseDocument() == null)
            driver.setDrivingLicenseDocument(existingDriver.getDrivingLicenseDocument());
    }

}
