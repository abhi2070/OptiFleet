package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

public class VehicleId extends UUIDBased implements EntityId{

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public VehicleId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static VehicleId fromString(String vehicleId) {
        return new VehicleId(UUID.fromString(vehicleId));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "VEHICLE", allowableValues = "VEHICLE")
    @Override
    public EntityType getEntityType() {
        return EntityType.VEHICLE;
    }

}
