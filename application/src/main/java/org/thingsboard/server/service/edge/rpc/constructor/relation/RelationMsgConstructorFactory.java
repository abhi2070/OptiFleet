
package org.thingsboard.server.service.edge.rpc.constructor.relation;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class RelationMsgConstructorFactory extends BaseMsgConstructorFactory<RelationMsgConstructorV1, RelationMsgConstructorV2> {

}
