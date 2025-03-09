
package org.thingsboard.server.service.edge.rpc.constructor.alarm;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class AlarmMsgConstructorFactory extends BaseMsgConstructorFactory<AlarmMsgConstructorV1, AlarmMsgConstructorV2> {

}
