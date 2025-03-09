
package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

public class NotificationTargetId extends UUIDBased implements EntityId {

    @JsonCreator
    public NotificationTargetId(@JsonProperty("id") UUID id) {
        super(id);
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "NOTIFICATION_TARGET", allowableValues = "NOTIFICATION_TARGET")
    @Override
    public EntityType getEntityType() {
        return EntityType.NOTIFICATION_TARGET;
    }

}
