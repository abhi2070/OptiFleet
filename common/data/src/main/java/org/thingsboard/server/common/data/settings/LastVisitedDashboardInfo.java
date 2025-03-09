
package org.thingsboard.server.common.data.settings;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@ApiModel
@Data
public class LastVisitedDashboardInfo extends AbstractUserDashboardInfo implements Serializable {

    private static final long serialVersionUID = -6461562426034242608L;

    @ApiModelProperty(position = 3, value = "Starred flag")
    private boolean starred;
    @ApiModelProperty(position = 4, value = "Last visit timestamp")
    private long lastVisited;

}
