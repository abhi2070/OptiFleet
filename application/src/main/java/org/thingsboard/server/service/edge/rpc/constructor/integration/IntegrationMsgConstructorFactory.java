package org.thingsboard.server.service.edge.rpc.constructor.integration;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class IntegrationMsgConstructorFactory extends BaseMsgConstructorFactory<IntegrationMsgConstructorV1, IntegrationMsgConstructorV2> {
}
