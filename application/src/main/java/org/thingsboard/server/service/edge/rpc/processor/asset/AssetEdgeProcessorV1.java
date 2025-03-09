
package org.thingsboard.server.service.edge.rpc.processor.asset;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.AssetUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.UUID;

@Component
@TbCoreComponent
public class AssetEdgeProcessorV1 extends AssetEdgeProcessor {

    @Override
    protected Asset constructAssetFromUpdateMsg(TenantId tenantId, AssetId assetId, AssetUpdateMsg assetUpdateMsg) {
        Asset asset = new Asset();
        asset.setTenantId(tenantId);
        asset.setName(assetUpdateMsg.getName());
        asset.setCreatedTime(Uuids.unixTimestamp(assetId.getId()));
        asset.setType(assetUpdateMsg.getType());
        asset.setLabel(assetUpdateMsg.hasLabel() ? assetUpdateMsg.getLabel() : null);
        asset.setAdditionalInfo(assetUpdateMsg.hasAdditionalInfo()
                ? JacksonUtil.toJsonNode(assetUpdateMsg.getAdditionalInfo()) : null);

        UUID assetProfileUUID = safeGetUUID(assetUpdateMsg.getAssetProfileIdMSB(), assetUpdateMsg.getAssetProfileIdLSB());
        asset.setAssetProfileId(assetProfileUUID != null ? new AssetProfileId(assetProfileUUID) : null);

        CustomerId customerId = safeGetCustomerId(assetUpdateMsg.getCustomerIdMSB(), assetUpdateMsg.getCustomerIdLSB());
        asset.setCustomerId(customerId);
        return asset;
    }

    @Override
    protected void setCustomerId(TenantId tenantId, CustomerId customerId, Asset asset, AssetUpdateMsg assetUpdateMsg) {
        CustomerId customerUUID = safeGetCustomerId(assetUpdateMsg.getCustomerIdMSB(), assetUpdateMsg.getCustomerIdLSB());
        asset.setCustomerId(customerUUID != null ? customerUUID : customerId);
    }
}
