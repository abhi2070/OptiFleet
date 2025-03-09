
package org.thingsboard.server.common.data.alarm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class AlarmCommentInfo extends AlarmComment {
    private static final long serialVersionUID = 2807343093519543377L;

    @ApiModelProperty(position = 19, value = "User first name", example = "John")
    private String firstName;

    @ApiModelProperty(position = 19, value = "User last name", example = "Brown")
    private String lastName;

    @ApiModelProperty(position = 19, value = "User email address", example = "johnBrown@gmail.com")
    private String email;

    public AlarmCommentInfo() {
        super();
    }

    public AlarmCommentInfo(AlarmComment alarmComment) {
        super(alarmComment);
    }

    public AlarmCommentInfo(AlarmComment alarmComment, String firstName, String lastName, String email) {
        super(alarmComment);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
