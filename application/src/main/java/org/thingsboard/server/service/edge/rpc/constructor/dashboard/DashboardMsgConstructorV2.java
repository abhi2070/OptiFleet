
package org.thingsboard.server.service.edge.rpc.constructor.dashboard;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.gen.edge.v1.DashboardUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class DashboardMsgConstructorV2 extends BaseDashboardMsgConstructor {

    @Override
    public DashboardUpdateMsg constructDashboardUpdatedMsg(UpdateMsgType msgType, Dashboard dashboard) {
        return DashboardUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(dashboard))
                .setIdMSB(dashboard.getId().getId().getMostSignificantBits())
                .setIdLSB(dashboard.getId().getId().getLeastSignificantBits()).build();
    }
}
