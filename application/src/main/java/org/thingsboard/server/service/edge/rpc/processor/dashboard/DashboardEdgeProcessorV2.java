
package org.thingsboard.server.service.edge.rpc.processor.dashboard;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.DashboardUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Primary
@Component
@TbCoreComponent
public class DashboardEdgeProcessorV2 extends DashboardEdgeProcessor {

    @Override
    protected Dashboard constructDashboardFromUpdateMsg(TenantId tenantId, DashboardId dashboardId, DashboardUpdateMsg dashboardUpdateMsg) {
        return JacksonUtil.fromString(dashboardUpdateMsg.getEntity(), Dashboard.class, true);
    }
}
