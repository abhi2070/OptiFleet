
package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

public class QueueId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public QueueId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static QueueId fromString(String queueId) {
        return new QueueId(UUID.fromString(queueId));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "QUEUE", allowableValues = "QUEUE")
    @Override
    public EntityType getEntityType() {
        return EntityType.QUEUE;
    }
}