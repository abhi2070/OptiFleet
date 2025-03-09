
package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import java.util.UUID;

@ApiModel
public class AlarmCommentId extends UUIDBased {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public AlarmCommentId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static AlarmCommentId fromString(String commentId) {
        return new AlarmCommentId(UUID.fromString(commentId));
    }
}
