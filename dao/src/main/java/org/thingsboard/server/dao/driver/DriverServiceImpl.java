package org.thingsboard.server.dao.driver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.common.data.id.DriverId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.AbstractCachedEntityService;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.Validator;

import java.util.Optional;

@Service("DriverDaoService")
@RequiredArgsConstructor
public class DriverServiceImpl extends AbstractEntityService implements DriverService {

    public static final String INCORRECT_DRIVER_ID = "Incorrect driverId ";
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";

    @Autowired
    private DriverDao driverDao;

    @Autowired
    private DataValidator<Driver> driverValidator;

    @Override
    public Driver findDriverById(TenantId tenantId, DriverId driverId) {
        Validator.validateId(driverId, INCORRECT_DRIVER_ID + driverId);
        return driverDao.findById(tenantId, driverId.getId());
    }

    @Override
    public Driver saveDriver(Driver driver) {
        return doSaveDriver(driver, true);
    }

    @Override
    public void deleteDriver(TenantId tenantId, DriverId driverId) {
        Validator.validateId(driverId, INCORRECT_DRIVER_ID + driverId);
        deleteEntityRelations(tenantId, driverId);
        try {
            driverDao.removeById(tenantId, driverId.getId());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public PageData<Driver> findDriversByTenantId(TenantId tenantId, PageLink pageLink) {
        Validator.validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        Validator.validatePageLink(pageLink);
        return driverDao.findDriversByTenantId(tenantId.getId(), pageLink);
    }

    private Driver doSaveDriver(Driver driver, boolean doValidate) {
        if (doValidate) {
            driverValidator.validate(driver, Driver::getTenantId);
        }
        try {
            Driver saved = driverDao.save(driver.getTenantId(), driver);
            return saved;
        } catch (Exception e) {
            checkConstraintViolation(e, "driving_license_number_unq_key", "Driver with such driving_license_number already exists!");
            throw e;
        }
    }

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findDriverById(tenantId, new DriverId(entityId.getId())));
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DRIVER;
    }

}
