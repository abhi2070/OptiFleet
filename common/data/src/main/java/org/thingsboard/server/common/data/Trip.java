package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TripId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.List;
import java.util.UUID;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trip extends BaseData<TripId> implements HasName, HasTenantId, ExportableEntity<TripId> {

    private static final long serialVersionUID = 2807343040519543363L;

    private TenantId tenantId;
    @NoXss
    @Length(fieldName = "name")
    private String name;
    @NoXss
    @Length(fieldName = "status")
    private String status;
    @NoXss
    @Length(fieldName = "startDate")
    private Long startDate;
    @NoXss
    @Length(fieldName = "endDate")
    private Long endDate;
    @NoXss
    @Length(fieldName = "vehicleType")
    private String vehicleType;
    @NoXss
    @Length(fieldName = "assignedVehicle")
    private UUID assignedVehicle;
    @NoXss
    @Length(fieldName = "assignedDriver")
    private List<UUID> assignedDriver;
    @NoXss
    @Length(fieldName = "startLocation")
    private Double startLocation;
    @NoXss
    @Length(fieldName = "destination")
    private Double destination;

    public Trip() {
        super();
    }

    public Trip(TripId id) {
        super(id);
    }

    public Trip(Trip trip) {
        super(trip);
        this.tenantId = trip.getTenantId();
        this.createdTime = trip.getCreatedTime();
        this.name = trip.getName();
        this.startDate = trip.getStartDate();
        this.endDate = trip.getEndDate();
        this.vehicleType = trip.getVehicleType();
        this.assignedVehicle = trip.getAssignedVehicle();
        this.assignedDriver = trip.getAssignedDriver();
        this.startLocation = trip.getStartLocation();
        this.destination = trip.getDestination();
        this.status = trip.getStatus();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the trip Id. " + "Specify this field to update the trip. " + "Omit this field to create new trip.")
    @Override
    public TripId getId() {
        return super.getId();
    }

    @Override
    public TripId getExternalId() {
        return null;
    }

    @Override
    public void setExternalId(TripId externalId) {

    }

    @ApiModelProperty(position = 2, value = "Timestamp of the trip creation, in milliseconds", example = "1634058704567", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id. Use 'assignTripToTenant' to change the Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @ApiModelProperty(position = 4, required = true, value = "Trip name", example = "Goa Tour")
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(position = 5, value = "Trip start date, in milliseconds", example = "1634058704567")
    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    @ApiModelProperty(position = 6, value = "Trip end date, in milliseconds", example = "1634058704567")
    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    @ApiModelProperty(position = 7, value = "", example = "")
    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    @ApiModelProperty(position = 8, value = "", example = "")
    public UUID getAssignedVehicle() {
        return assignedVehicle;
    }

    public void setAssignedVehicle(UUID assignedVehicle) {
        this.assignedVehicle = assignedVehicle;
    }

    @ApiModelProperty(position = 9, value = "", example = "")
    public List<UUID> getAssignedDriver() {
        return assignedDriver;
    }

    public void setAssignedDriver(List<UUID> assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    @ApiModelProperty(position = 10, value = "", example = "")
    public Double getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Double startLocation) {
        this.startLocation = startLocation;
    }

    @ApiModelProperty(position = 11, value = "", example = "")
    public Double getDestination() {
        return destination;
    }

    public void setDestination(Double destination) {
        this.destination = destination;
    }

    @ApiModelProperty(position = 12, value = "", example = "")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Trip [tenantId=");
        builder.append(tenantId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", startDate=");
        builder.append(startDate);
        builder.append(", endDate=");
        builder.append(endDate);
        builder.append(", vehicleType=");
        builder.append(vehicleType);
        builder.append(", assignedVehicle=");
        builder.append(assignedVehicle);
        builder.append(", assignedDriver=");
        builder.append(assignedDriver);
        builder.append(", startLocation=");
        builder.append(startLocation);
        builder.append(", destination=");
        builder.append(destination);
        builder.append(", status=");
        builder.append(status);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
