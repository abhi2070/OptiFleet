
package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

public class TenantProfileId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public TenantProfileId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static TenantProfileId fromString(String tenantProfileId) {
        return new TenantProfileId(UUID.fromString(tenantProfileId));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "TENANT_PROFILE", allowableValues = "TENANT_PROFILE")
    @Override
    public EntityType getEntityType() {
        return EntityType.TENANT_PROFILE;
    }
}
