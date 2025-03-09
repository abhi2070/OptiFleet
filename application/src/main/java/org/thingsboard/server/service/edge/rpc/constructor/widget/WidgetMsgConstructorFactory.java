
package org.thingsboard.server.service.edge.rpc.constructor.widget;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class WidgetMsgConstructorFactory extends BaseMsgConstructorFactory<WidgetMsgConstructorV1, WidgetMsgConstructorV2> {

}
