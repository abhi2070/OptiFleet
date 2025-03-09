
package org.thingsboard.server.common.data.alarm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.Collections;
import java.util.List;

@Builder
@Data
public class AlarmPropagationInfo {

    public static AlarmPropagationInfo EMPTY = new AlarmPropagationInfo(false, false, false, Collections.emptyList());

    @ApiModelProperty(position = 1, value = "Propagation flag to specify if alarm should be propagated to parent entities of alarm originator", example = "true")
    private boolean propagate;
    @ApiModelProperty(position = 2, value = "Propagation flag to specify if alarm should be propagated to the owner (tenant or customer) of alarm originator", example = "true")
    private boolean propagateToOwner;
    @ApiModelProperty(position = 3, value = "Propagation flag to specify if alarm should be propagated to the tenant entity", example = "true")
    private boolean propagateToTenant;
    @NoXss
    @ApiModelProperty(position = 4, value = "JSON array of relation types that should be used for propagation. " +
            "By default, 'propagateRelationTypes' array is empty which means that the alarm will be propagated based on any relation type to parent entities. " +
            "This parameter should be used only in case when 'propagate' parameter is set to true, otherwise, 'propagateRelationTypes' array will be ignored.")
    private List<String> propagateRelationTypes;

}
