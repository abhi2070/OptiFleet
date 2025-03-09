package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;
import static org.thingsboard.server.dao.model.ModelConstants.EXTERNAL_ID_PROPERTY;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public class AbstractRolesEntity<T extends Roles> extends BaseSqlEntity<T> {

    @Column(name = ROLES_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = ROLES_CUSTOMER_ID_PROPERTY)
    private UUID customerId;


    @Column(name = ROLES_NAME_PROPERTY)
    private String name;
    @Column(name = ROLES_TYPE_PROPERTY)
    private String type;
    @Type(type = "json")
    @Column(name = ModelConstants.ROLES_ADDITIONAL_INFO_PROPERTY)
    private JsonNode additionalInfo;
    @Column(name = EXTERNAL_ID_PROPERTY)
    private UUID externalId;

    public AbstractRolesEntity() {
        super();
    }

    public AbstractRolesEntity(Roles roles) {
        if (roles.getId() != null) {
            this.setUuid(roles.getId().getId());
        }
        this.setCreatedTime(roles.getCreatedTime());
        if (roles.getTenantId() != null) {
            this.tenantId = roles.getTenantId().getId();
        }
        if (roles.getCustomerId() != null) {
            this.customerId = roles.getCustomerId().getId();
        }
        this.name = roles.getName();
        this.type = roles.getType();
        this.additionalInfo = roles.getAdditionalInfo();
        if (roles.getExternalId() != null) {
            this.externalId = roles.getExternalId().getId();
        }
    }

    public AbstractRolesEntity(RolesEntity rolesEntity) {
        this.setId(rolesEntity.getId());
        this.setCreatedTime(rolesEntity.getCreatedTime());
        this.tenantId = rolesEntity.getTenantId();
        this.customerId = rolesEntity.getCustomerId();
        this.type = rolesEntity.getType();
        this.name = rolesEntity.getName();
        this.additionalInfo = rolesEntity.getAdditionalInfo();
        this.externalId = rolesEntity.getExternalId();
    }

    protected Roles toRoles() {
        Roles roles = new Roles(new RolesId(id));
        roles.setCreatedTime(createdTime);
        if (tenantId != null) {
            roles.setTenantId(TenantId.fromUUID(tenantId));
        }
        if (customerId != null) {
            roles.setCustomerId(new CustomerId(customerId));
        }
        roles.setName(name);
        roles.setType(type);
        roles.setAdditionalInfo(additionalInfo);
        if (externalId != null) {
            roles.setExternalId(new RolesId(externalId));
        }
        return roles;
    }

    @Override
    public T toData() {
        return null;
    }
}
