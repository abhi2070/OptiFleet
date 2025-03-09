
package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SystemInfo {
    @ApiModelProperty(position = 1, value = "Is monolith.")
    private boolean isMonolith;
    @ApiModelProperty(position = 2, value = "System data.")
    private List<SystemInfoData> systemData;
}
