
package org.thingsboard.server.service.edge.rpc.constructor.widget;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.gen.edge.v1.WidgetTypeUpdateMsg;
import org.thingsboard.server.gen.edge.v1.WidgetsBundleUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.List;

@Component
@TbCoreComponent
public class WidgetMsgConstructorV2 extends BaseWidgetMsgConstructor {

    @Override
    public WidgetsBundleUpdateMsg constructWidgetsBundleUpdateMsg(UpdateMsgType msgType, WidgetsBundle widgetsBundle, List<String> widgets) {
        return WidgetsBundleUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(widgetsBundle))
                .setWidgets(JacksonUtil.toString(widgets))
                .setIdMSB(widgetsBundle.getId().getId().getMostSignificantBits())
                .setIdLSB(widgetsBundle.getId().getId().getLeastSignificantBits()).build();
    }

    @Override
    public WidgetTypeUpdateMsg constructWidgetTypeUpdateMsg(UpdateMsgType msgType, WidgetTypeDetails widgetTypeDetails, EdgeVersion edgeVersion) {
        return WidgetTypeUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(widgetTypeDetails))
                .setIdMSB(widgetTypeDetails.getId().getId().getMostSignificantBits())
                .setIdLSB(widgetTypeDetails.getId().getId().getLeastSignificantBits()).build();
    }
}
