
package org.thingsboard.server.service.edge.rpc.constructor.dashboard;

import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.gen.edge.v1.DashboardUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface DashboardMsgConstructor extends MsgConstructor {

    DashboardUpdateMsg constructDashboardUpdatedMsg(UpdateMsgType msgType, Dashboard dashboard);

    DashboardUpdateMsg constructDashboardDeleteMsg(DashboardId dashboardId);
}
