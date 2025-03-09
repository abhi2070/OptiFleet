
package org.thingsboard.server.common.data.edge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EdgeInstructions {

    @ApiModelProperty(position = 1, value = "Markdown with install/upgrade instructions")
    private String instructions;
}
