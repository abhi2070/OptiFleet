
package org.thingsboard.server.service.edge.rpc.processor.widget;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.gen.edge.v1.WidgetTypeUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.widget.WidgetMsgConstructor;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;

@Slf4j
@Component
@TbCoreComponent
public class WidgetTypeEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg convertWidgetTypeEventToDownlink(EdgeEvent edgeEvent, EdgeVersion edgeVersion) {
        WidgetTypeId widgetTypeId = new WidgetTypeId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (edgeEvent.getAction()) {
            case ADDED:
            case UPDATED:
                WidgetTypeDetails widgetTypeDetails = widgetTypeService.findWidgetTypeDetailsById(edgeEvent.getTenantId(), widgetTypeId);
                if (widgetTypeDetails != null) {
                    UpdateMsgType msgType = getUpdateMsgType(edgeEvent.getAction());
                    WidgetTypeUpdateMsg widgetTypeUpdateMsg =
                            ((WidgetMsgConstructor) widgetMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructWidgetTypeUpdateMsg(msgType, widgetTypeDetails, edgeVersion);
                    downlinkMsg = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addWidgetTypeUpdateMsg(widgetTypeUpdateMsg)
                            .build();
                }
                break;
            case DELETED:
                WidgetTypeUpdateMsg widgetTypeUpdateMsg =
                        ((WidgetMsgConstructor) widgetMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructWidgetTypeDeleteMsg(widgetTypeId);
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addWidgetTypeUpdateMsg(widgetTypeUpdateMsg)
                        .build();
                break;
        }
        return downlinkMsg;
    }
}
