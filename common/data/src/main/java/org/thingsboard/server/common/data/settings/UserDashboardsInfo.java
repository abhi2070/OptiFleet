
package org.thingsboard.server.common.data.settings;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApiModel
@Data
@AllArgsConstructor
public class UserDashboardsInfo implements Serializable {

    private static final long serialVersionUID = 2628320657987010348L;
    public static final UserDashboardsInfo EMPTY = new UserDashboardsInfo(Collections.emptyList(), Collections.emptyList());

    @ApiModelProperty(position = 1, value = "List of last visited dashboards.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private List<LastVisitedDashboardInfo> last;

    @ApiModelProperty(position = 2, value = "List of starred dashboards.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private List<StarredDashboardInfo> starred;

    public UserDashboardsInfo() {
        this(new ArrayList<>(), new ArrayList<>());
    }
}
