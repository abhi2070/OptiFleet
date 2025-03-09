
package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@ApiModel
public class EdgeUpgradeMessage implements Serializable {

    private static final long serialVersionUID = 2872965507642822989L;

    @ApiModelProperty(position = 1, value = "Mapping for upgrade versions and upgrade strategy (next ver).")
    private final Map<String, EdgeUpgradeInfo> edgeVersions;
}
