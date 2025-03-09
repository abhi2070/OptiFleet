
package org.thingsboard.server.service.edge.rpc.constructor.queue;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.queue.Queue;
import org.thingsboard.server.gen.edge.v1.QueueUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class QueueMsgConstructorV2 extends BaseQueueMsgConstructor {

    @Override
    public QueueUpdateMsg constructQueueUpdatedMsg(UpdateMsgType msgType, Queue queue) {
        return QueueUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(queue))
                .setIdMSB(queue.getId().getId().getMostSignificantBits())
                .setIdLSB(queue.getId().getId().getLeastSignificantBits()).build();
    }
}
