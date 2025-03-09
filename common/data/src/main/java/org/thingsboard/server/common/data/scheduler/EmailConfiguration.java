package org.thingsboard.server.common.data.scheduler;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "Mail Configuration")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailConfiguration implements Serializable {

    @ApiModelProperty(position = 1, value = "Recipient's email Address")
    private String toAddress;

    @ApiModelProperty(position = 2, value = "CC email Address")
    private String cc;

    @ApiModelProperty(position = 3, value = "BCC email Address")
    private String bcc;

    @ApiModelProperty(position = 4, value = "Email subject")
    private String subject;

    @ApiModelProperty(position = 5, value = "Email Body, can be text, image, file, etc.")
    private Object body;

}
