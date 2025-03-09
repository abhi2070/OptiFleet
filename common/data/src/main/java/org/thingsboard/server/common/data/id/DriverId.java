package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

@ApiModel
public class DriverId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public DriverId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static DriverId fromString(String driverId) {
        return new DriverId(UUID.fromString(driverId));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "DRIVER", allowableValues = "DRIVER")
    @Override
    public EntityType getEntityType() {
        return EntityType.DRIVER;
    }
}
