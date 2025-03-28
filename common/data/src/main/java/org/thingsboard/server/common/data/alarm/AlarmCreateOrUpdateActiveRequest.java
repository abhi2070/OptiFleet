
package org.thingsboard.server.common.data.alarm;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class AlarmCreateOrUpdateActiveRequest implements AlarmModificationRequest {

    @NotNull
    @ApiModelProperty(position = 1, value = "JSON object with Tenant Id", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private TenantId tenantId;
    @ApiModelProperty(position = 2, value = "JSON object with Customer Id", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private CustomerId customerId;
    @NotNull
    @ApiModelProperty(position = 3, required = true, value = "representing type of the Alarm", example = "High Temperature Alarm")
    @Length(fieldName = "type")
    private String type;
    @NotNull
    @ApiModelProperty(position = 4, required = true, value = "JSON object with alarm originator id")
    private EntityId originator;
    @NotNull
    @ApiModelProperty(position = 5, required = true, value = "Alarm severity", example = "CRITICAL")
    private AlarmSeverity severity;
    @ApiModelProperty(position = 6, value = "Timestamp of the alarm start time, in milliseconds", example = "1634058704565")
    private long startTs;
    @ApiModelProperty(position = 7, value = "Timestamp of the alarm end time(last time update), in milliseconds", example = "1634111163522")
    private long endTs;
    @NoXss
    @ApiModelProperty(position = 8, value = "JSON object with alarm details")
    private JsonNode details;
    @Valid
    @ApiModelProperty(position = 9, value = "JSON object with propagation details")
    private AlarmPropagationInfo propagation;

    private UserId userId;

    private AlarmId edgeAlarmId;

    public static AlarmCreateOrUpdateActiveRequest fromAlarm(Alarm a) {
        return fromAlarm(a, null);
    }

    public static AlarmCreateOrUpdateActiveRequest fromAlarm(Alarm a, UserId userId) {
        return fromAlarm(a, userId, null);
    }

    public static AlarmCreateOrUpdateActiveRequest fromAlarm(Alarm a, UserId userId, AlarmId edgeAlarmId) {
        return AlarmCreateOrUpdateActiveRequest.builder()
                .tenantId(a.getTenantId())
                .customerId(a.getCustomerId())
                .type(a.getType())
                .originator(a.getOriginator())
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
                .edgeAlarmId(edgeAlarmId)
                .build();
    }

}
