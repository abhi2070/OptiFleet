
package org.thingsboard.server.common.data.widget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

@Data
public class BaseWidgetType extends BaseData<WidgetTypeId> implements HasName, HasTenantId {

    private static final long serialVersionUID = 8388684344603660756L;

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private TenantId tenantId;
    @NoXss
    @Length(fieldName = "fqn")
    @ApiModelProperty(position = 5, value = "Unique FQN that is used in dashboards as a reference widget type", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String fqn;
    @NoXss
    @Length(fieldName = "name")
    @ApiModelProperty(position = 6, value = "Widget name used in search and UI")
    private String name;

    @ApiModelProperty(position = 7, value = "Whether widget type is deprecated.", example = "true")
    private boolean deprecated;

    public BaseWidgetType() {
        super();
    }

    public BaseWidgetType(WidgetTypeId id) {
        super(id);
    }

    public BaseWidgetType(BaseWidgetType widgetType) {
        super(widgetType);
        this.tenantId = widgetType.getTenantId();
        this.fqn = widgetType.getFqn();
        this.name = widgetType.getName();
        this.deprecated = widgetType.isDeprecated();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the Widget Type Id. " +
            "Specify this field to update the Widget Type. " +
            "Referencing non-existing Widget Type Id will cause error. " +
            "Omit this field to create new Widget Type." )
    @Override
    public WidgetTypeId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the Widget Type creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }
}
