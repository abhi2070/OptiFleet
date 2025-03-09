
package org.thingsboard.server.service.edge.rpc.constructor.entityview;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class EntityViewMsgConstructorFactory extends BaseMsgConstructorFactory<EntityViewMsgConstructorV1, EntityViewMsgConstructorV2> {

}
