
package org.thingsboard.server.common.data;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.objects.TelemetryEntityView;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

/**
 * Created by Victor Basanets on 8/27/2017.
 */

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EntityView extends BaseDataWithAdditionalInfo<EntityViewId>
        implements HasName, HasTenantId, HasCustomerId, ExportableEntity<EntityViewId> {

    private static final long serialVersionUID = 5582010124562018986L;

    @ApiModelProperty(position = 7, value = "JSON object with the referenced Entity Id (Device or Asset).")
    private EntityId entityId;
    private TenantId tenantId;
    private CustomerId customerId;
    @NoXss
    @Length(fieldName = "name")
    @ApiModelProperty(position = 5, required = true, value = "Entity View name", example = "A4B72CCDFF33")
    private String name;
    @NoXss
    @Length(fieldName = "type")
    @ApiModelProperty(position = 6, required = true, value = "Device Profile Name", example = "Temperature Sensor")
    private String type;
    @ApiModelProperty(position = 8, value = "Set of telemetry and attribute keys to expose via Entity View.")
    private TelemetryEntityView keys;
    @ApiModelProperty(position = 9, value = "Represents the start time of the interval that is used to limit access to target device telemetry. Customer will not be able to see entity telemetry that is outside the specified interval;")
    private long startTimeMs;
    @ApiModelProperty(position = 10, value = "Represents the end time of the interval that is used to limit access to target device telemetry. Customer will not be able to see entity telemetry that is outside the specified interval;")
    private long endTimeMs;

    private EntityViewId externalId;

    public EntityView() {
        super();
    }

    public EntityView(EntityViewId id) {
        super(id);
    }

    public EntityView(EntityView entityView) {
        super(entityView);
        this.entityId = entityView.getEntityId();
        this.tenantId = entityView.getTenantId();
        this.customerId = entityView.getCustomerId();
        this.name = entityView.getName();
        this.type = entityView.getType();
        this.keys = entityView.getKeys();
        this.startTimeMs = entityView.getStartTimeMs();
        this.endTimeMs = entityView.getEndTimeMs();
        this.externalId = entityView.getExternalId();
    }

    @ApiModelProperty(position = 4, value = "JSON object with Customer Id. Use 'assignEntityViewToCustomer' to change the Customer Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public CustomerId getCustomerId() {
        return customerId;
    }

    @Override
    public String getName() {
        return name;
    }

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public TenantId getTenantId() {
        return tenantId;
    }

    @ApiModelProperty(position = 1, value = "JSON object with the Entity View Id. " +
            "Specify this field to update the Entity View. " +
            "Referencing non-existing Entity View Id will cause error. " +
            "Omit this field to create new Entity View." )
    @Override
    public EntityViewId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the Entity View creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 11, value = "Additional parameters of the device", dataType = "com.fasterxml.jackson.databind.JsonNode")
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

}
