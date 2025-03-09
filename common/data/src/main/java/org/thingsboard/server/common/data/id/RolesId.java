package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

public class RolesId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public RolesId(@JsonProperty("id") UUID id) {
        super(id);
    }



    public static RolesId fromString(String rolesId) {
        return new RolesId(UUID.fromString(rolesId));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "ROLES", allowableValues = "ROLES")
    @Override
    public EntityType getEntityType() {
        return EntityType.ROLES;
    }
}
