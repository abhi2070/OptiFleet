
package org.thingsboard.server.service.edge.rpc.processor.asset;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.AssetUpdateMsg;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;

@Slf4j
public abstract class BaseAssetProcessor extends BaseEdgeProcessor {

    protected Pair<Boolean, Boolean> saveOrUpdateAsset(TenantId tenantId, AssetId assetId, AssetUpdateMsg assetUpdateMsg) {
        boolean created = false;
        boolean assetNameUpdated = false;
        assetCreationLock.lock();
        try {
            Asset asset = constructAssetFromUpdateMsg(tenantId, assetId, assetUpdateMsg);
            if (asset == null) {
                throw new RuntimeException("[{" + tenantId + "}] assetUpdateMsg {" + assetUpdateMsg + " } cannot be converted to asset");
            }
            Asset assetById = assetService.findAssetById(tenantId, assetId);
            if (assetById == null) {
                created = true;
                asset.setId(null);
            } else {
                asset.setId(assetId);
            }
            String assetName = asset.getName();
            Asset assetByName = assetService.findAssetByTenantIdAndName(tenantId, assetName);
            if (assetByName != null && !assetByName.getId().equals(assetId)) {
                assetName = assetName + "_" + StringUtils.randomAlphanumeric(15);
                log.warn("[{}] Asset with name {} already exists. Renaming asset name to {}",
                        tenantId, asset.getName(), assetName);
                assetNameUpdated = true;
            }
            asset.setName(assetName);
            setCustomerId(tenantId, created ? null : assetById.getCustomerId(), asset, assetUpdateMsg);

            assetValidator.validate(asset, Asset::getTenantId);
            if (created) {
                asset.setId(assetId);
            }
            assetService.saveAsset(asset, false);
        } catch (Exception e) {
            log.error("[{}] Failed to process asset update msg [{}]", tenantId, assetUpdateMsg, e);
            throw e;
        } finally {
            assetCreationLock.unlock();
        }
        return Pair.of(created, assetNameUpdated);
    }

    protected abstract Asset constructAssetFromUpdateMsg(TenantId tenantId, AssetId assetId, AssetUpdateMsg assetUpdateMsg);

    protected abstract void setCustomerId(TenantId tenantId, CustomerId customerId, Asset asset, AssetUpdateMsg assetUpdateMsg);
}
