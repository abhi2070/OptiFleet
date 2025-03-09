
package org.thingsboard.server.service.edge.rpc.constructor.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.dao.resource.ImageService;
import org.thingsboard.server.gen.edge.v1.DashboardUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class DashboardMsgConstructorV1 extends BaseDashboardMsgConstructor {

    @Autowired
    private ImageService imageService;

    @Override
    public DashboardUpdateMsg constructDashboardUpdatedMsg(UpdateMsgType msgType, Dashboard dashboard) {
        dashboard = JacksonUtil.clone(dashboard);
        imageService.inlineImagesForEdge(dashboard);
        DashboardUpdateMsg.Builder builder = DashboardUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(dashboard.getId().getId().getMostSignificantBits())
                .setIdLSB(dashboard.getId().getId().getLeastSignificantBits())
                .setTitle(dashboard.getTitle())
                .setConfiguration(JacksonUtil.toString(dashboard.getConfiguration()))
                .setMobileHide(dashboard.isMobileHide());
        if (dashboard.getAssignedCustomers() != null) {
            builder.setAssignedCustomers(JacksonUtil.toString(dashboard.getAssignedCustomers()));
        }
        if (dashboard.getImage() != null) {
            builder.setImage(dashboard.getImage());
        }
        if (dashboard.getMobileOrder() != null) {
            builder.setMobileOrder(dashboard.getMobileOrder());
        }
        return builder.build();
    }

}
