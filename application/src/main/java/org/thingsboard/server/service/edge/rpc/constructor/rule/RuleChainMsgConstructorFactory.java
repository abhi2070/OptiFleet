
package org.thingsboard.server.service.edge.rpc.constructor.rule;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class RuleChainMsgConstructorFactory extends BaseMsgConstructorFactory<RuleChainMsgConstructorV1, RuleChainMsgConstructorV2> {

}
