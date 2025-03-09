
package org.thingsboard.server.service.edge.rpc.constructor.settings;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.gen.edge.v1.AdminSettingsUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class AdminSettingsMsgConstructorV2 implements AdminSettingsMsgConstructor {

    @Override
    public AdminSettingsUpdateMsg constructAdminSettingsUpdateMsg(AdminSettings adminSettings) {
        return AdminSettingsUpdateMsg.newBuilder().setEntity(JacksonUtil.toString(adminSettings)).build();
    }
}
