package org.thingsboard.server.common.data.driver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.DriverId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Driver extends BaseData<DriverId> implements HasName, HasTenantId, ExportableEntity<DriverId> {

    private static final long serialVersionUID = 2807343040519543363L;

    private TenantId tenantId;
    @NoXss
    @Length(fieldName = "name")
    private String name;
    @NoXss
    @Length(fieldName = "status")
    private String status;
    @NoXss
    @Length(fieldName = "gender")
    private String gender;
    @NoXss
    @Length(fieldName = "service_start_date")
    private Long serviceStartDate;
    @NoXss
    @Length(fieldName = "date_of_birth")
    private Long dateOfBirth;
    @NoXss
    @Length(fieldName = "driving_license_number")
    private String drivingLicenseNumber;
    @NoXss
    @Length(fieldName = "driving_license_validity")
    private Long drivingLicenseValidity;
    @NoXss
    @Length(fieldName = "profile_photo")
    private byte[] profilePhoto;
    @NoXss
    @Length(fieldName = "driving_license_document")
    private byte[] drivingLicenseDocument;

    public Driver() {
        super();
    }

    public Driver(DriverId id) {
        super(id);
    }

    public Driver(Driver driver) {
        super(driver);
        this.tenantId = driver.getTenantId();
        this.createdTime = driver.getCreatedTime();
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

    @ApiModelProperty(position = 1, value = "JSON object with the driver Id. " + "Specify this field to update the driver. " + "Omit this field to create new driver.")
    @Override
    public DriverId getId() {
        return super.getId();
    }

    @Override
    public DriverId getExternalId() {
        return null;
    }

    @Override
    public void setExternalId(DriverId externalId) {

    }

    @ApiModelProperty(position = 2, value = "Timestamp of the driver creation, in milliseconds", example = "1634058704567", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id. Use 'assignDriverToTenant' to change the Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @ApiModelProperty(position = 4, required = true, value = "Driver name", example = "John Doe")
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(position = 5, value = "", example = "")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ApiModelProperty(position = 6, value = "", example = "")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @ApiModelProperty(position = 7, value = "", example = "")
    public Long getServiceStartDate() {
        return serviceStartDate;
    }

    public void setServiceStartDate(Long serviceStartDate) {
        this.serviceStartDate = serviceStartDate;
    }

    @ApiModelProperty(position = 8, value = "", example = "")
    public Long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @ApiModelProperty(position = 9, value = "", example = "")
    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    @ApiModelProperty(position = 10, value = "", example = "")
    public Long getDrivingLicenseValidity() {
        return drivingLicenseValidity;
    }

    public void setDrivingLicenseValidity(Long drivingLicenseValidity) {
        this.drivingLicenseValidity = drivingLicenseValidity;
    }

    @ApiModelProperty(position = 11, value = "", example = "")
    public byte[] getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(byte[] profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    @ApiModelProperty(position = 12, value = "", example = "")
    public byte[] getDrivingLicenseDocument() {
        return drivingLicenseDocument;
    }

    public void setDrivingLicenseDocument(byte[] drivingLicenseDocument) {
        this.drivingLicenseDocument = drivingLicenseDocument;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Driver [tenantId=");
        builder.append(tenantId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", status=");
        builder.append(status);
        builder.append(", gender=");
        builder.append(gender);
        builder.append(", serviceStartDate=");
        builder.append(serviceStartDate);
        builder.append(", dateOfBirth=");
        builder.append(dateOfBirth);
        builder.append(", drivingLicenseNumber=");
        builder.append(drivingLicenseNumber);
        builder.append(", drivingLicenseValidity=");
        builder.append(drivingLicenseValidity);
        builder.append(", profilePhoto=");
        builder.append(profilePhoto);
        builder.append(", drivingLicenseDocument=");
        builder.append(drivingLicenseDocument);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }
}
