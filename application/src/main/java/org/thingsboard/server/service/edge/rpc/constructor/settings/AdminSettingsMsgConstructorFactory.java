
package org.thingsboard.server.service.edge.rpc.constructor.settings;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.BaseMsgConstructorFactory;

@Component
@TbCoreComponent
public class AdminSettingsMsgConstructorFactory extends BaseMsgConstructorFactory<AdminSettingsMsgConstructorV1, AdminSettingsMsgConstructorV2> {

}
