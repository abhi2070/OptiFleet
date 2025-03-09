
package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.UserId;

@ApiModel
@Data
@AllArgsConstructor
public class UserEmailInfo implements HasId<UserId> {

    @ApiModelProperty(position = 1, value = "User id")
    private UserId id;
    @ApiModelProperty(position = 2, value = "User email", example = "john@gmail.com")
    private String email;
    @ApiModelProperty(position = 3, value = "User first name", example = "John")
    private String firstName;
    @ApiModelProperty(position = 4, value = "User last name", example = "Brown")
    private String lastName;

}
