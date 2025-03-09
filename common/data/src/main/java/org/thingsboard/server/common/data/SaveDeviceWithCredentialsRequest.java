
package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.security.DeviceCredentials;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class SaveDeviceWithCredentialsRequest {

    @ApiModelProperty(position = 1, value = "The JSON with device entity.", required = true)
    @NotNull
    private final Device device;
    @ApiModelProperty(position = 2, value = "The JSON with credentials entity.", required = true)
    @NotNull
    private final DeviceCredentials credentials;

}
