
package org.thingsboard.server.service.edge.rpc.constructor.resource;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class ResourceMsgConstructorFactory extends BaseMsgConstructorFactory<ResourceMsgConstructorV1, ResourceMsgConstructorV2> {

}
