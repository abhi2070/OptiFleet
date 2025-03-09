package org.thingsboard.server.common.data.data_converter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "Registration Settings")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MetaData {

    private static final long serialVersionUID = -1307613974597312465L;

    @ApiModelProperty(position = 1, value = "Domain settings for self registration. ")
    private String integrationName;

    @ApiModelProperty(position = 1, value = "Domain settings for self registration. ")
    private String decoder;

    @ApiModelProperty(position = 1, value = "Domain settings for self registration. ")
    private String payload;

}
