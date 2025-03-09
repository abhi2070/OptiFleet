package org.thingsboard.server.dao.model.sql;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.VehicleId;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public class AbstractVehicleEntity <T extends Vehicle> extends BaseSqlEntity<T> {

    @Column(name = VEHICLE_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = VEHICLE_NUMBER_PROPERTY, unique = true)
    private String vehiclenumber;

    @Column(name = VEHICLE_TYPE_PROPERTY)
    private String type;

    @Column(name = VEHICLE_NEXT_SERVICE_PROPERTY)
    private Long next_service;

    @Column(name = VEHICLE_STATUS_PROPERTY)
    private String status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vehicle_devices", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "device_id")
    private List<UUID> device;

    @Column(name = VEHICLE_RC_PROPERTY)
    private byte[] registrationCertificate;

    @Column(name = VEHICLE_INSURANCE_PROPERTY)
    private byte[] insuranceCertificate;

    @Column(name = VEHICLE_PUC_PROPERTY)
    private byte[] pucCertificate;

    @Column(name = VEHICLE_REQUIRED_PERMITS_PROPERTY)
    private byte[] requiredPermits;

    public AbstractVehicleEntity() {
        super();
    }

    public AbstractVehicleEntity(Vehicle Vehicle) {
        if (Vehicle.getId() != null) {
            this.setUuid(Vehicle.getId().getId());
        }
        this.setCreatedTime(Vehicle.getCreatedTime());
        if (Vehicle.getTenantId() != null) {
            this.tenantId = Vehicle.getTenantId().getId();
        }
        this.vehiclenumber = Vehicle.getVehiclenumber();
        this.type = Vehicle.getType();
        this.next_service = Vehicle.getNextService();
        this.status = Vehicle.getStatus();
        this.device = Vehicle.getDevice();
        this.registrationCertificate = Vehicle.getRegistrationCertificate();
        this.insuranceCertificate = Vehicle.getInsuranceCertificate();
        this.pucCertificate = Vehicle.getPucCertificate();
        this.requiredPermits = Vehicle.getRequiredPermits();
    }

    public AbstractVehicleEntity(VehicleEntity vehicleEntity) {
        this.setId(vehicleEntity.getId());
        this.setCreatedTime(vehicleEntity.getCreatedTime());
        this.tenantId = vehicleEntity.getTenantId();
        this.vehiclenumber = vehicleEntity.getVehiclenumber();
        this.type = vehicleEntity.getType();
        this.next_service = vehicleEntity.getNext_service();
        this.status = vehicleEntity.getStatus();
        this.device = vehicleEntity.getDevice();
        this.registrationCertificate = vehicleEntity.getRegistrationCertificate();
        this.insuranceCertificate = vehicleEntity.getInsuranceCertificate();
        this.pucCertificate = vehicleEntity.getPucCertificate();
        this.requiredPermits = vehicleEntity.getRequiredPermits();
    }

    protected Vehicle toVehicle() {
        Vehicle Vehicle = new Vehicle(new VehicleId(id));
        Vehicle.setCreatedTime(createdTime);
        if (tenantId != null) {
            Vehicle.setTenantId(TenantId.fromUUID(tenantId));
        }
        Vehicle.setVehiclenumber(vehiclenumber);
        Vehicle.setType(type);
        Vehicle.setNextService(next_service);
        Vehicle.setStatus(status);
        Vehicle.setDevice(device);
        Vehicle.setRegistrationCertificate(registrationCertificate);
        Vehicle.setInsuranceCertificate(insuranceCertificate);
        Vehicle.setPucCertificate(pucCertificate);
        Vehicle.setRequiredPermits(requiredPermits);
        return Vehicle;
    }

    @Override
    public T toData() {
        return null;
    }
}
