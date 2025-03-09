package org.thingsboard.server.common.data.roles;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import javax.persistence.Column;
import java.util.Optional;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class Roles extends BaseDataWithAdditionalInfo<RolesId> implements HasType, HasTenantId, HasCustomerId,  ExportableEntity<RolesId> {

    private static final long serialVersionUID = 2807343040519543363L;
    private TenantId tenantId;
    private CustomerId customerId;

    @NoXss
    @Length(fieldName = "name")
    private String name;
    @NoXss
    @Length(fieldName = "type")
    private String type;
    @Getter
    @Setter
    private RolesId externalId;

    public Roles() {
        super();
    }

    public Roles(RolesId id) {
        super(id);
    }

    public Roles(Roles roles) {
        super(roles);
        this.tenantId = roles.getTenantId();
        this.customerId = roles.getCustomerId();
        this.name = roles.getName();
        this.type = roles.getType();
        this.externalId = roles.getExternalId();
    }

    public void update(Roles roles) {
        this.tenantId = roles.getTenantId();
        this.customerId = roles.getCustomerId();
        this.name = roles.getName();
        this.type = roles.getType();
        Optional.ofNullable(roles.getAdditionalInfo()).ifPresent(this::setAdditionalInfo);
        this.externalId = roles.getExternalId();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the roles Id. " + "Specify this field to update the roles. " + "Referencing non-existing roles Id will cause error. " + "Omit this field to create new roles.")
    @Override
    public RolesId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the role creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public TenantId getTenantId() {
        return tenantId;
    }

    @ApiModelProperty(position = 4, value = "JSON object with Customer Id. Use 'assignRolesToCustomer' to change the Customer Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @ApiModelProperty(position = 5, required = true, value = "Unique Role Name in scope of Tenant", example = "Empire State Building")
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(position = 6, value = "Roles type", example = "Building")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @ApiModelProperty(position = 9, value = "Additional parameters of the role", dataType = "com.fasterxml.jackson.databind.JsonNode")
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Roles [tenantId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", type=");
        builder.append(type);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }


}
