package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

@ApiModel
public class IntegrationId extends UUIDBased implements EntityId{

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public IntegrationId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static IntegrationId fromString(String integrationId) {
        return new IntegrationId(UUID.fromString(integrationId));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "INTEGRATION", allowableValues = "INTEGRATION")
    @Override
    public EntityType getEntityType() {
        return EntityType.INTEGRATION;
    }
}
