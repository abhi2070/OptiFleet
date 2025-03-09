
package org.thingsboard.server.service.edge.rpc.constructor.dashboard;

import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.gen.edge.v1.DashboardUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;

public abstract class BaseDashboardMsgConstructor implements DashboardMsgConstructor {

    @Override
    public DashboardUpdateMsg constructDashboardDeleteMsg(DashboardId dashboardId) {
        return DashboardUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(dashboardId.getId().getMostSignificantBits())
                .setIdLSB(dashboardId.getId().getLeastSignificantBits()).build();
    }
}
