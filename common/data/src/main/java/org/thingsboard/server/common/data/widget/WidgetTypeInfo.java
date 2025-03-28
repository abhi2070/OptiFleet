
package org.thingsboard.server.common.data.widget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.validation.NoXss;

@Data
public class WidgetTypeInfo extends BaseWidgetType {

    @ApiModelProperty(position = 8, value = "Base64 encoded widget thumbnail", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String image;
    @NoXss
    @ApiModelProperty(position = 9, value = "Description of the widget type", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String description;
    @NoXss
    @ApiModelProperty(position = 10, value = "Tags of the widget type", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String[] tags;
    @NoXss
    @ApiModelProperty(position = 11, value = "Type of the widget (timeseries, latest, control, alarm or static)", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String widgetType;

    public WidgetTypeInfo() {
        super();
    }

    public WidgetTypeInfo(WidgetTypeId id) {
        super(id);
    }

    public WidgetTypeInfo(BaseWidgetType baseWidgetType) {
        super(baseWidgetType);
    }

    public WidgetTypeInfo(WidgetTypeInfo widgetTypeInfo) {
        super(widgetTypeInfo);
        this.image = widgetTypeInfo.getImage();
        this.description = widgetTypeInfo.getDescription();
        this.tags = widgetTypeInfo.getTags();
        this.widgetType = widgetTypeInfo.getWidgetType();
    }

    public WidgetTypeInfo(WidgetTypeDetails widgetTypeDetails) {
        super(widgetTypeDetails);
        this.image = widgetTypeDetails.getImage();
        this.description = widgetTypeDetails.getDescription();
        this.tags = widgetTypeDetails.getTags();
        if (widgetTypeDetails.getDescriptor() != null && widgetTypeDetails.getDescriptor().has("type")) {
            this.widgetType = widgetTypeDetails.getDescriptor().get("type").asText();
        } else {
            this.widgetType = "";
        }
    }
}
