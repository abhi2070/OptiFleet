
package org.thingsboard.server.common.data.alarm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@ApiModel
public class AlarmInfo extends Alarm {

    private static final long serialVersionUID = 2807343093519543363L;

    @Getter
    @Setter
    @ApiModelProperty(position = 19, value = "Alarm originator name", example = "Thermostat")
    private String originatorName;

    @Getter
    @Setter
    @ApiModelProperty(position = 20, value = "Alarm originator label", example = "Thermostat label")
    private String originatorLabel;

    @Getter
    @Setter
    @ApiModelProperty(position = 21, value = "Alarm assignee")
    private AlarmAssignee assignee;

    public AlarmInfo() {
        super();
    }

    public AlarmInfo(Alarm alarm) {
        super(alarm);
    }

    public AlarmInfo(AlarmInfo alarmInfo) {
        super(alarmInfo);
        this.originatorName = alarmInfo.originatorName;
        this.originatorLabel = alarmInfo.originatorLabel;
        this.assignee = alarmInfo.getAssignee();
    }

    public AlarmInfo(Alarm alarm, String originatorName, String originatorLabel, AlarmAssignee assignee) {
        super(alarm);
        this.originatorName = originatorName;
        this.originatorLabel = originatorLabel;
        this.assignee = assignee;
    }

}
