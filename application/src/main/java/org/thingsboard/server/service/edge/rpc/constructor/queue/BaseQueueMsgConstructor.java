
package org.thingsboard.server.service.edge.rpc.constructor.queue;

import org.thingsboard.server.common.data.id.QueueId;
import org.thingsboard.server.gen.edge.v1.QueueUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;

public abstract class BaseQueueMsgConstructor implements QueueMsgConstructor {

    @Override
    public QueueUpdateMsg constructQueueDeleteMsg(QueueId queueId) {
        return QueueUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(queueId.getId().getMostSignificantBits())
                .setIdLSB(queueId.getId().getLeastSignificantBits()).build();
    }
}
