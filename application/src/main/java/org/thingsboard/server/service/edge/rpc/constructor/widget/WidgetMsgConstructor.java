
package org.thingsboard.server.service.edge.rpc.constructor.widget;

import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.gen.edge.v1.WidgetTypeUpdateMsg;
import org.thingsboard.server.gen.edge.v1.WidgetsBundleUpdateMsg;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

import java.util.List;

public interface WidgetMsgConstructor extends MsgConstructor {

    WidgetsBundleUpdateMsg constructWidgetsBundleUpdateMsg(UpdateMsgType msgType, WidgetsBundle widgetsBundle, List<String> widgets);

    WidgetsBundleUpdateMsg constructWidgetsBundleDeleteMsg(WidgetsBundleId widgetsBundleId);

    WidgetTypeUpdateMsg constructWidgetTypeUpdateMsg(UpdateMsgType msgType, WidgetTypeDetails widgetTypeDetails, EdgeVersion edgeVersion);

    WidgetTypeUpdateMsg constructWidgetTypeDeleteMsg(WidgetTypeId widgetTypeId);
}
