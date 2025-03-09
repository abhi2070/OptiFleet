
package org.thingsboard.server.service.edge.rpc.constructor.ota;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class OtaPackageMsgConstructorFactory extends BaseMsgConstructorFactory<OtaPackageMsgConstructorV1, OtaPackageMsgConstructorV2> {

}
