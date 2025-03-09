
package org.thingsboard.server.common.data.settings;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@ApiModel
@Data
public class StarredDashboardInfo extends AbstractUserDashboardInfo implements Serializable {

    private static final long serialVersionUID = -7830828696329673361L;
    @ApiModelProperty(position = 4, value = "Starred timestamp")
    private long starredAt;

}
