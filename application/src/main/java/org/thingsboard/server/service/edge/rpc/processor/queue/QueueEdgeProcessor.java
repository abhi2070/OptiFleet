
package org.thingsboard.server.service.edge.rpc.processor.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.QueueId;
import org.thingsboard.server.common.data.queue.Queue;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.QueueUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.queue.QueueMsgConstructor;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;

@Slf4j
@Component
@TbCoreComponent
public class QueueEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg convertQueueEventToDownlink(EdgeEvent edgeEvent, EdgeVersion edgeVersion) {
        QueueId queueId = new QueueId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (edgeEvent.getAction()) {
            case ADDED:
            case UPDATED:
                Queue queue = queueService.findQueueById(edgeEvent.getTenantId(), queueId);
                if (queue != null) {
                    UpdateMsgType msgType = getUpdateMsgType(edgeEvent.getAction());
                    QueueUpdateMsg queueUpdateMsg = ((QueueMsgConstructor)
                            queueMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructQueueUpdatedMsg(msgType, queue);
                    downlinkMsg = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addQueueUpdateMsg(queueUpdateMsg)
                            .build();
                }
                break;
            case DELETED:
                QueueUpdateMsg queueDeleteMsg = ((QueueMsgConstructor)
                        queueMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructQueueDeleteMsg(queueId);
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addQueueUpdateMsg(queueDeleteMsg)
                        .build();
                break;
        }
        return downlinkMsg;
    }
}
