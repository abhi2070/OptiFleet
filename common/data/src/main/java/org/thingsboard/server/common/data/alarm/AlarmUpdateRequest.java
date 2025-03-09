
package org.thingsboard.server.common.data.alarm;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.validation.NoXss;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class AlarmUpdateRequest implements AlarmModificationRequest {

    @NotNull
    @ApiModelProperty(position = 1, value = "JSON object with Tenant Id", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private TenantId tenantId;
    @NotNull
    @ApiModelProperty(position = 2, value = "JSON object with the alarm Id. " +
            "Specify this field to update the alarm. " +
            "Referencing non-existing alarm Id will cause error. " +
            "Omit this field to create new alarm.")
    private AlarmId alarmId;
    @NotNull
    @ApiModelProperty(position = 3, required = true, value = "Alarm severity", example = "CRITICAL")
    private AlarmSeverity severity;
    @ApiModelProperty(position = 4, value = "Timestamp of the alarm start time, in milliseconds", example = "1634058704565")
    private long startTs;
    @ApiModelProperty(position = 5, value = "Timestamp of the alarm end time(last time update), in milliseconds", example = "1634111163522")
    private long endTs;
    @NoXss
    @ApiModelProperty(position = 6, value = "JSON object with alarm details")
    private JsonNode details;
    @Valid
    @ApiModelProperty(position = 7, value = "JSON object with propagation details")
    private AlarmPropagationInfo propagation;

    private UserId userId;

    public static AlarmUpdateRequest fromAlarm(Alarm a) {
        return fromAlarm(a, null);
    }

    public static AlarmUpdateRequest fromAlarm(Alarm a, UserId userId) {
        return AlarmUpdateRequest.builder()
                .tenantId(a.getTenantId())
                .alarmId(a.getId())
                .severity((a.getSeverity()))
                .startTs(a.getStartTs())
                .endTs(a.getEndTs())
                .details(a.getDetails())
                .propagation(AlarmPropagationInfo.builder()
                        .propagate(a.isPropagate())
                        .propagateToOwner(a.isPropagateToOwner())
                        .propagateToTenant(a.isPropagateToTenant())
                        .propagateRelationTypes(a.getPropagateRelationTypes()).build())
                .userId(userId)
                .build();
    }
}
