
package org.thingsboard.server.service.edge.rpc.constructor.customer;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class CustomerMsgConstructorFactory extends BaseMsgConstructorFactory<CustomerMsgConstructorV1, CustomerMsgConstructorV2> {

}
