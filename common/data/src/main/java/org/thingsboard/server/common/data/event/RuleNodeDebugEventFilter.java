
package org.thingsboard.server.common.data.event;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.StringUtils;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel
public class RuleNodeDebugEventFilter extends DebugEventFilter {

    @ApiModelProperty(position = 2, value = "String value representing msg direction type (incoming to entity or outcoming from entity)", allowableValues = "IN, OUT")
    protected String msgDirectionType;
    @ApiModelProperty(position = 3, value = "String value representing the entity id in the event body (originator of the message)", example = "de9d54a0-2b7a-11ec-a3cc-23386423d98f")
    protected String entityId;
    @ApiModelProperty(position = 4, value = "String value representing the entity type", allowableValues = "DEVICE")
    protected String entityType;
    @ApiModelProperty(position = 5, value = "String value representing the message id in the rule engine", example = "de9d54a0-2b7a-11ec-a3cc-23386423d98f")
    protected String msgId;
    @ApiModelProperty(position = 6, value = "String value representing the message type", example = "POST_TELEMETRY_REQUEST")
    protected String msgType;
    @ApiModelProperty(position = 7, value = "String value representing the type of message routing", example = "Success")
    protected String relationType;
    @ApiModelProperty(position = 8, value = "The case insensitive 'contains' filter based on data (key and value) for the message.", example = "humidity")
    protected String dataSearch;
    @ApiModelProperty(position = 9, value = "The case insensitive 'contains' filter based on metadata (key and value) for the message.", example = "deviceName")
    protected String metadataSearch;

    @Override
    public EventType getEventType() {
        return EventType.DEBUG_RULE_NODE;
    }

    @Override
    public boolean isNotEmpty() {
        return super.isNotEmpty() || !StringUtils.isEmpty(msgDirectionType) || !StringUtils.isEmpty(entityId)
                || !StringUtils.isEmpty(entityType) || !StringUtils.isEmpty(msgId) || !StringUtils.isEmpty(msgType) ||
                !StringUtils.isEmpty(relationType) || !StringUtils.isEmpty(dataSearch) || !StringUtils.isEmpty(metadataSearch);
    }
}
