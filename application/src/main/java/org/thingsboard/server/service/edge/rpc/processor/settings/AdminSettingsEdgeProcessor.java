
package org.thingsboard.server.service.edge.rpc.processor.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.gen.edge.v1.AdminSettingsUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.settings.AdminSettingsMsgConstructor;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;

@Slf4j
@Component
@TbCoreComponent
public class AdminSettingsEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg convertAdminSettingsEventToDownlink(EdgeEvent edgeEvent, EdgeVersion edgeVersion) {
        AdminSettings adminSettings = JacksonUtil.convertValue(edgeEvent.getBody(), AdminSettings.class);
        if (adminSettings == null) {
            return null;
        }
        AdminSettingsUpdateMsg adminSettingsUpdateMsg = ((AdminSettingsMsgConstructor)
                adminSettingsMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructAdminSettingsUpdateMsg(adminSettings);
        return DownlinkMsg.newBuilder()
                .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                .addAdminSettingsUpdateMsg(adminSettingsUpdateMsg)
                .build();
    }
}
