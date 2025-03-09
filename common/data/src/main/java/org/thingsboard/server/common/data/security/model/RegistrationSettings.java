package org.thingsboard.server.common.data.security.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "Registration Settings")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegistrationSettings {

    private static final long serialVersionUID = -1307613974597312465L;

    @ApiModelProperty(position = 1, value = "Domain settings for self registration. ")
    private DomainSettings domainSettings;

    @ApiModelProperty(position = 2, value = "General settings for self registration. ")
    private GeneralSettings generalSettings;

    @ApiModelProperty(position = 3, value = "Mobile settings for self registration. ")
    private MobileSettings mobileSettings;

    @ApiModelProperty(position = 4, value = "Privacy policy text for self registration. ")
    private String privacyPolicy;

    @ApiModelProperty(position = 5, value = "Terms of use text for self registration. ")
    private String termsOfUse;

    @ApiModelProperty(position = 6, value = "Show Privacy policy text for self registration. ")
    private boolean showPrivacyPolicy;

    @ApiModelProperty(position = 7, value = "Show Terms of use text for self registration. ")
    private boolean showTermOfUse;

}
