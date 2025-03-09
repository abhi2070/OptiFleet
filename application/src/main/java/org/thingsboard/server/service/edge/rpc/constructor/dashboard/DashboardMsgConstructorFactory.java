
package org.thingsboard.server.service.edge.rpc.constructor.dashboard;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class DashboardMsgConstructorFactory extends BaseMsgConstructorFactory<DashboardMsgConstructorV1, DashboardMsgConstructorV2> {

}
