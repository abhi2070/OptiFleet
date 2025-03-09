
package org.thingsboard.server.service.edge.rpc.constructor.queue;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class QueueMsgConstructorFactory extends BaseMsgConstructorFactory<QueueMsgConstructorV1, QueueMsgConstructorV2> {

}
