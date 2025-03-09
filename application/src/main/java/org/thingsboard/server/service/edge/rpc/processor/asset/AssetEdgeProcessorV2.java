
package org.thingsboard.server.service.edge.rpc.processor.asset;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.AssetUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Primary
@Component
@TbCoreComponent
public class AssetEdgeProcessorV2 extends AssetEdgeProcessor {

    @Override
    protected Asset constructAssetFromUpdateMsg(TenantId tenantId, AssetId assetId, AssetUpdateMsg assetUpdateMsg) {
        return JacksonUtil.fromString(assetUpdateMsg.getEntity(), Asset.class, true);
    }

    @Override
    protected void setCustomerId(TenantId tenantId, CustomerId customerId, Asset asset, AssetUpdateMsg assetUpdateMsg) {
        CustomerId customerUUID = asset.getCustomerId() != null ? asset.getCustomerId() : customerId;
        asset.setCustomerId(customerUUID);
    }
}
