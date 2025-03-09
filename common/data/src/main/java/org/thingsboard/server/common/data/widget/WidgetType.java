
package org.thingsboard.server.common.data.widget;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.WidgetTypeId;

@Data
public class WidgetType extends BaseWidgetType {

    @ApiModelProperty(position = 8, value = "Complex JSON object that describes the widget type")
    private transient JsonNode descriptor;

    public WidgetType() {
        super();
    }

    public WidgetType(WidgetTypeId id) {
        super(id);
    }

    public WidgetType(BaseWidgetType baseWidgetType) {
        super(baseWidgetType);
    }

    public WidgetType(WidgetType widgetType) {
        super(widgetType);
        this.descriptor = widgetType.getDescriptor();
    }

}
