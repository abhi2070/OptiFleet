package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Trip;
import org.thingsboard.server.common.data.id.DriverId;
import org.thingsboard.server.common.data.id.TripId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.BaseEntity;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.model.ModelConstants.*;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractTripEntity<T extends Trip> extends BaseSqlEntity<T> implements BaseEntity<T> {

    @Column(name = TRIP_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = TRIP_NAME)
    private String name;

    @Column(name = TRIP_STATUS)
    private String status;

    @Column(name = TRIP_START_DATE)
    private Long startDate;

    @Column(name = TRIP_END_DATE)
    private Long endDate;

    @Column(name = TRIP_VEHICLE_TYPE)
    private String vehicleType;

    @Column(name = TRIP_ASSIGNED_VEHICLE)
    private UUID assignedVehicle;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TRIP_DRIVERS, joinColumns = @JoinColumn(name = TRIP_ID))
    @Column(name = TRIP_ASSIGNED_DRIVER_ID)
    private List<UUID> assignedDriver;

    @Column(name = TRIP_START_LOCATION)
    private Double startLocation;

    @Column(name = TRIP_DESTINATION)
    private Double destination;


    public AbstractTripEntity() {
        super();
    }

    public AbstractTripEntity(Trip trip) {
        if (trip.getId() != null) {
            this.setUuid(trip.getUuidId());
        }
        this.setCreatedTime(trip.getCreatedTime());
        if (trip.getTenantId() != null) {
            this.tenantId = trip.getTenantId().getId();
        }
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

    public AbstractTripEntity(TripEntity tripEntity) {
        this.setId(tripEntity.getId());
        this.setCreatedTime(tripEntity.getCreatedTime());
        this.tenantId = tripEntity.getTenantId();
        this.name = tripEntity.getName();
        this.startDate = tripEntity.getStartDate();
        this.endDate = tripEntity.getEndDate();
        this.vehicleType = tripEntity.getVehicleType();
        this.assignedVehicle = tripEntity.getAssignedVehicle();
        this.assignedDriver = tripEntity.getAssignedDriver();
        this.startLocation = tripEntity.getStartLocation();
        this.destination = tripEntity.getDestination();
        this.status = tripEntity.getStatus();
    }

    protected Trip toTrip() {
        Trip trip = new Trip(new TripId(id));
        trip.setCreatedTime(createdTime);
        if (tenantId != null) {
            trip.setTenantId(TenantId.fromUUID(tenantId));
        }
        trip.setName(name);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setVehicleType(vehicleType);
        trip.setAssignedVehicle(assignedVehicle);
        trip.setAssignedDriver(assignedDriver);
        trip.setStartLocation(startLocation);
        trip.setDestination(destination);
        trip.setStatus(status);
        return trip;
    }

}
