package org.thingsboard.server.common.data.security.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@ApiModel
@Data
public class GeneralSettings implements Serializable {

    @ApiModelProperty(position = 2, value = "Notification mail." )
    private String notificationMail;
    @ApiModelProperty(position = 2, value = "Sign up Message." )
    private String signUpPageMessage;
    @ApiModelProperty(position = 2, value = "Particular dashboard for an user." )
    private String dashboard;
    @ApiModelProperty(position = 2, value = "Dashboard Fullscreen for an user." )
    private boolean dashboardFullScreen;
}
