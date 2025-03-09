
package org.thingsboard.server.service.edge.rpc.processor.device.profile;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.DeviceProfileUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.UUID;

@Primary
@Component
@TbCoreComponent
public class DeviceProfileEdgeProcessorV2 extends DeviceProfileEdgeProcessor {

    @Override
    protected DeviceProfile constructDeviceProfileFromUpdateMsg(TenantId tenantId, DeviceProfileId deviceProfileId, DeviceProfileUpdateMsg deviceProfileUpdateMsg) {
        return JacksonUtil.fromString(deviceProfileUpdateMsg.getEntity(), DeviceProfile.class, true);
    }

    @Override
    protected void setDefaultRuleChainId(TenantId tenantId, DeviceProfile deviceProfile, RuleChainId ruleChainId) {
        deviceProfile.setDefaultRuleChainId(ruleChainId);
    }

    @Override
    protected void setDefaultEdgeRuleChainId(DeviceProfile deviceProfile, RuleChainId ruleChainId, DeviceProfileUpdateMsg deviceProfileUpdateMsg) {
        UUID defaultEdgeRuleChainUUID = ruleChainId != null ? ruleChainId.getId() : null;
        deviceProfile.setDefaultEdgeRuleChainId(defaultEdgeRuleChainUUID != null ? new RuleChainId(defaultEdgeRuleChainUUID) : null);
    }

    @Override
    protected void setDefaultDashboardId(TenantId tenantId, DashboardId dashboardId, DeviceProfile deviceProfile, DeviceProfileUpdateMsg deviceProfileUpdateMsg) {
        UUID defaultDashboardUUID = deviceProfile.getDefaultDashboardId() != null ? deviceProfile.getDefaultDashboardId().getId() : (dashboardId != null ? dashboardId.getId() : null);
        deviceProfile.setDefaultDashboardId(defaultDashboardUUID != null ? new DashboardId(defaultDashboardUUID) : null);
    }
}
