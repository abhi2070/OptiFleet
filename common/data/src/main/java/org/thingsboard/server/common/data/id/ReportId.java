package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;
import java.util.UUID;

@ApiModel
public class ReportId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public ReportId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static ReportId fromString(String reportId) {
        return new ReportId(UUID.fromString(reportId));
    }

    @Override
    @ApiModelProperty(position = 2, required = true, value = "string", example = "REPORT", allowableValues = "REPORT")
    public EntityType getEntityType() { return EntityType.REPORT; }
}
