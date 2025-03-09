
package org.thingsboard.server.service.edge.rpc.constructor.user;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class UserMsgConstructorFactory extends BaseMsgConstructorFactory<UserMsgConstructorV1, UserMsgConstructorV2> {

}
