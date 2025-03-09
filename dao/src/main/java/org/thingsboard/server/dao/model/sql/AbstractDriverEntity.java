package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.common.data.id.DriverId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.BaseEntity;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;
import static org.thingsboard.server.dao.model.ModelConstants.*;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractDriverEntity<T extends Driver> extends BaseSqlEntity<T> implements BaseEntity<T> {

    @Column(name = DRIVER_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = DRIVER_NAME)
    private String name;

    @Column(name = DRIVER_STATUS)
    private String status;

    @Column(name = DRIVER_GENDER)
    private String gender;

    @Column(name = DRIVER_SERVICE_START_DATE)
    private Long serviceStartDate;

    @Column(name = DRIVER_DATE_OF_BIRTH)
    private Long dateOfBirth;

    @Column(name = DRIVER_DRIVING_LICENSE_NUMBER)
    private String drivingLicenseNumber;

    @Column(name = DRIVER_DRIVING_LICENSE_VALIDITY)
    private Long drivingLicenseValidity;

    @Column(name = DRIVER_PROFILE_PHOTO)
    private byte[] profilePhoto;

    @Column(name = DRIVER_DRIVING_LICENSE_DOCUMENT)
    private byte[] drivingLicenseDocument;

    public AbstractDriverEntity() {
        super();
    }

    public AbstractDriverEntity(Driver driver) {
        if (driver.getId() != null) {
            this.setUuid(driver.getUuidId());
        }
        this.setCreatedTime(driver.getCreatedTime());
        if (driver.getTenantId() != null) {
            this.tenantId = driver.getTenantId().getId();
        }
        this.name = driver.getName();
        this.status = driver.getStatus();
        this.gender = driver.getGender();
        this.serviceStartDate = driver.getServiceStartDate();
        this.dateOfBirth = driver.getDateOfBirth();
        this.drivingLicenseNumber = driver.getDrivingLicenseNumber();
        this.drivingLicenseValidity = driver.getDrivingLicenseValidity();
        this.profilePhoto = driver.getProfilePhoto();
        this.drivingLicenseDocument = driver.getDrivingLicenseDocument();
    }

    public AbstractDriverEntity(DriverEntity driverEntity) {
        this.setId(driverEntity.getId());
        this.setCreatedTime(driverEntity.getCreatedTime());
        this.tenantId = driverEntity.getTenantId();
        this.name = driverEntity.getName();
        this.status = driverEntity.getStatus();
        this.gender = driverEntity.getGender();
        this.serviceStartDate = driverEntity.getServiceStartDate();
        this.dateOfBirth = driverEntity.getDateOfBirth();
        this.drivingLicenseNumber = driverEntity.getDrivingLicenseNumber();
        this.drivingLicenseValidity = driverEntity.getDrivingLicenseValidity();
        this.profilePhoto = driverEntity.getProfilePhoto();
        this.drivingLicenseDocument = driverEntity.getDrivingLicenseDocument();
    }

    protected Driver toDriver() {
        Driver driver = new Driver(new DriverId(id));
        driver.setCreatedTime(createdTime);
        if (tenantId != null) {
            driver.setTenantId(TenantId.fromUUID(tenantId));
        }
        driver.setName(name);
        driver.setStatus(status);
        driver.setGender(gender);
        driver.setServiceStartDate(serviceStartDate);
        driver.setDateOfBirth(dateOfBirth);
        driver.setDrivingLicenseNumber(drivingLicenseNumber);
        driver.setDrivingLicenseValidity(drivingLicenseValidity);
        driver.setProfilePhoto(profilePhoto);
        driver.setDrivingLicenseDocument(drivingLicenseDocument);
        return driver;
    }

}
