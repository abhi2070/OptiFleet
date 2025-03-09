
package org.thingsboard.server.service.edge.rpc.constructor.queue;

import org.thingsboard.server.common.data.id.QueueId;
import org.thingsboard.server.common.data.queue.Queue;
import org.thingsboard.server.gen.edge.v1.QueueUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface QueueMsgConstructor extends MsgConstructor {

    QueueUpdateMsg constructQueueUpdatedMsg(UpdateMsgType msgType, Queue queue);

    QueueUpdateMsg constructQueueDeleteMsg(QueueId queueId);
}
