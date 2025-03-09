package org.thingsboard.server.common.data.security.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@ApiModel
@Data
public class DomainSettings implements Serializable {

    @ApiModelProperty(position = 1, value = "Url domain name. " )
    private String domainName;
    @ApiModelProperty(position = 1, value = "ReCaptcha Version. " )
    private String version;
    @ApiModelProperty(position = 1, value = "ReCaptcha Log Action Name. " )
    private String logActionName;
    @ApiModelProperty(position = 1, value = "ReCaptcha Site Key. " )
    private String siteKey;
    @ApiModelProperty(position = 1, value = "ReCaptcha Secret Key. " )
    private String secretKey;

}
