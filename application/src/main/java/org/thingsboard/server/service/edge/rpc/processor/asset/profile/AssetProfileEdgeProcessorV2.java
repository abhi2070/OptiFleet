
package org.thingsboard.server.service.edge.rpc.processor.asset.profile;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.AssetProfileUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.UUID;

@Primary
@Component
@TbCoreComponent
public class AssetProfileEdgeProcessorV2 extends AssetProfileEdgeProcessor {

    @Override
    protected AssetProfile constructAssetProfileFromUpdateMsg(TenantId tenantId, AssetProfileId assetProfileId, AssetProfileUpdateMsg assetProfileUpdateMsg) {
        return JacksonUtil.fromString(assetProfileUpdateMsg.getEntity(), AssetProfile.class, true);
    }

    @Override
    protected void setDefaultRuleChainId(TenantId tenantId, AssetProfile assetProfile, RuleChainId ruleChainId) {
        assetProfile.setDefaultRuleChainId(ruleChainId);
    }

    @Override
    protected void setDefaultEdgeRuleChainId(AssetProfile assetProfile, RuleChainId ruleChainId, AssetProfileUpdateMsg assetProfileUpdateMsg) {
        UUID defaultEdgeRuleChainUUID = ruleChainId != null ? ruleChainId.getId() : null;
        assetProfile.setDefaultEdgeRuleChainId(defaultEdgeRuleChainUUID != null ? new RuleChainId(defaultEdgeRuleChainUUID) : null);
    }

    @Override
    protected void setDefaultDashboardId(TenantId tenantId, DashboardId dashboardId, AssetProfile assetProfile, AssetProfileUpdateMsg assetProfileUpdateMsg) {
        UUID defaultDashboardUUID = assetProfile.getDefaultDashboardId() != null ? assetProfile.getDefaultDashboardId().getId() : (dashboardId != null ? dashboardId.getId() : null);
        assetProfile.setDefaultDashboardId(defaultDashboardUUID != null ? new DashboardId(defaultDashboardUUID) : null);
    }
}
