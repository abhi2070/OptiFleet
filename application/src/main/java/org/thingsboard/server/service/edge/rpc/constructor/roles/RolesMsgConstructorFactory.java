package org.thingsboard.server.service.edge.rpc.constructor.roles;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;


@Component
@TbCoreComponent
public class RolesMsgConstructorFactory extends BaseMsgConstructorFactory<RolesMsgConstructorV1, RolesMsgConstructorV2> {
}
