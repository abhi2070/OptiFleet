package org.thingsboard.server.common.data.vehicle;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.VehicleId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class Vehicle extends BaseData<VehicleId> implements HasTenantId, ExportableEntity<VehicleId> {

    private static final long serialVersionUID = 2807343040519543363L;
    private TenantId tenantId;

    @NoXss
    @Length(fieldName = "vehicle_number")
    private String vehiclenumber;

    @NoXss
    @Length(fieldName = "type")
    private String type;

    @NoXss
    @Length(fieldName = "next_service")
    private Long nextService;

    @NoXss
    @Length(fieldName = "status")
    private String status;

    @NoXss
    @Length(fieldName = "device")
    private List<UUID> device;

    @NoXss
    @Length(fieldName = "registration_certificate")
    private byte[] registrationCertificate;

    @NoXss
    @Length(fieldName = "insurance_certificate")
    private byte[] insuranceCertificate;

    @NoXss
    @Length(fieldName = "puc_certificate")
    private byte[] pucCertificate;

    @NoXss
    @Length(fieldName = "required_permits")
    private byte[] requiredPermits;

    public Vehicle() {
        super();
    }

    public Vehicle(VehicleId id) {
        super(id);
    }

    public Vehicle(Vehicle vehicle) {
        super(vehicle);
        this.tenantId = vehicle.getTenantId();
        this.vehiclenumber = vehicle.getVehiclenumber();
        this.type = vehicle.getType();
        this.nextService = vehicle.getNextService();
        this.status = vehicle.getStatus();
        this.device = vehicle.getDevice();
        this.registrationCertificate = vehicle.getRegistrationCertificate();
        this.insuranceCertificate = vehicle.getInsuranceCertificate();
        this.pucCertificate = vehicle.getPucCertificate();
        this.requiredPermits = vehicle.getRequiredPermits();
    }

    public void update(Vehicle vehicle) {
        this.tenantId = vehicle.getTenantId();
        this.vehiclenumber = vehicle.getVehiclenumber();
        this.type = vehicle.getType();
        this.nextService = vehicle.getNextService();
        this.status = vehicle.getStatus();
        this.device = vehicle.getDevice();
        this.registrationCertificate = vehicle.getRegistrationCertificate();
        this.insuranceCertificate = vehicle.getInsuranceCertificate();
        this.pucCertificate = vehicle.getPucCertificate();
        this.requiredPermits = vehicle.getRequiredPermits();
    }

    @Override
    public VehicleId getId() {
        return super.getId();
    }

    public String getVehiclenumber() {return vehiclenumber;}

    public void setVehiclenumber(String vehiclenumber) {this.vehiclenumber = vehiclenumber;}

    public Long getNextService() {return nextService;}

    public void setNextService(Long next_service) {this.nextService = next_service;}

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public List<UUID> getDevice() {return device;}

    public void setDevice(List<UUID> device) {this.device = device;}

    @Override
    public VehicleId getExternalId() {return null;}

    @Override
    public void setExternalId(VehicleId externalId) {}

    public byte[] getRegistrationCertificate() {return registrationCertificate;}

    public void setRegistrationCertificate(byte[] registrationCertificate) {this.registrationCertificate = registrationCertificate;}

    public byte[] getInsuranceCertificate() {return insuranceCertificate;}

    public void setInsuranceCertificate(byte[] insuranceCertificate) {this.insuranceCertificate = insuranceCertificate;}

    public byte[] getPucCertificate() {return pucCertificate;}

    public void setPucCertificate(byte[] pucCertificate) {this.pucCertificate = pucCertificate;}

    public byte[] getRequiredPermits() {return requiredPermits;}

    public void setRequiredPermits(byte[] requiredPermits) {this.requiredPermits = requiredPermits;}

    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Vehicle [tenantId=");
        builder.append(tenantId);
        builder.append(", vehiclenumber=");
        builder.append(vehiclenumber);
        builder.append(", type=");
        builder.append(type);
        builder.append(", next_service=");
        builder.append(nextService);
        builder.append(", status=");
        builder.append(status);
        builder.append(", device=");
        builder.append(device);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String getName() {
        return vehiclenumber;
    }
}
