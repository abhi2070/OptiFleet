
package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class UpdateMessage implements Serializable {

    @ApiModelProperty(position = 1, value = "'True' if new platform update is available.")
    private final boolean updateAvailable;
    @ApiModelProperty(position = 2, value = "Current ThingsBoard version.")
    private final String currentVersion;
    @ApiModelProperty(position = 3, value = "Latest ThingsBoard version.")
    private final String latestVersion;
    @ApiModelProperty(position = 4, value = "Upgrade instructions URL.")
    private final String upgradeInstructionsUrl;
    @ApiModelProperty(position = 5, value = "Current ThingsBoard version release notes URL.")
    private final String currentVersionReleaseNotesUrl;
    @ApiModelProperty(position = 6, value = "Latest ThingsBoard version release notes URL.")
    private final String latestVersionReleaseNotesUrl;

}
