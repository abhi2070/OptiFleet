
package org.thingsboard.server.service.edge.rpc.constructor.tenant;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class TenantMsgConstructorFactory extends BaseMsgConstructorFactory<TenantMsgConstructorV1, TenantMsgConstructorV2> {

}
