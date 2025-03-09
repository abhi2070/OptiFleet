package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

public class SchedulerId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public SchedulerId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static SchedulerId fromString(String schedulerId) {
        return new SchedulerId(UUID.fromString(schedulerId));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "SCHEDULER", allowableValues = "SCHEDULER")
    @Override
    public EntityType getEntityType() {
        return EntityType.SCHEDULER;
    }
}
