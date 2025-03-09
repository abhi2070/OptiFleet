
package org.thingsboard.server.service.edge.rpc.constructor.asset;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class AssetMsgConstructorFactory extends BaseMsgConstructorFactory<AssetMsgConstructorV1, AssetMsgConstructorV2> {

}
