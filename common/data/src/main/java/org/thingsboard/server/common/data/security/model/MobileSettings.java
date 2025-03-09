package org.thingsboard.server.common.data.security.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@ApiModel
@Data
public class MobileSettings implements Serializable {

    @ApiModelProperty(position = 3, value = "Mobile application package name." )
    private String applicationPackage;
    @ApiModelProperty(position = 3, value = "Mobile application secret key." )
    private String applicationSecret;
    @ApiModelProperty(position = 3, value = "Mobile application secret key." )
    private String applicationUrlScheme;
    @ApiModelProperty(position = 3, value = "Mobile application url hostname." )
    private String applicationUrlHostname;
}
