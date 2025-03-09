
package org.thingsboard.server.common.data.settings;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.HasTitle;

import java.io.Serializable;
import java.util.UUID;

@ApiModel
@Data
public abstract class AbstractUserDashboardInfo implements HasTitle, Serializable {

    private static final long serialVersionUID = -6461562426034242608L;

    @ApiModelProperty(position = 1, value = "JSON object with Dashboard id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private UUID id;
    @ApiModelProperty(position = 2, value = "Title of the dashboard.")
    private String title;

}
