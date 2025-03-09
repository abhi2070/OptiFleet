
package org.thingsboard.server.service.edge.rpc.constructor.device;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class DeviceMsgConstructorFactory extends BaseMsgConstructorFactory<DeviceMsgConstructorV1, DeviceMsgConstructorV2> {

}
