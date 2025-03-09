
package org.thingsboard.server.dao.asset;

import org.thingsboard.server.common.data.EntityInfo;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.asset.AssetProfileInfo;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

public interface AssetProfileService extends EntityDaoService {

    AssetProfile findAssetProfileById(TenantId tenantId, AssetProfileId assetProfileId);

    AssetProfile findAssetProfileById(TenantId tenantId, AssetProfileId assetProfileId, boolean putInCache);

    AssetProfile findAssetProfileByName(TenantId tenantId, String profileName);

    AssetProfile findAssetProfileByName(TenantId tenantId, String profileName, boolean putInCache);

    AssetProfileInfo findAssetProfileInfoById(TenantId tenantId, AssetProfileId assetProfileId);

    AssetProfile saveAssetProfile(AssetProfile assetProfile, boolean doValidate);

    AssetProfile saveAssetProfile(AssetProfile assetProfile);

    void deleteAssetProfile(TenantId tenantId, AssetProfileId assetProfileId);

    PageData<AssetProfile> findAssetProfiles(TenantId tenantId, PageLink pageLink);

    PageData<AssetProfileInfo> findAssetProfileInfos(TenantId tenantId, PageLink pageLink);

    AssetProfile findOrCreateAssetProfile(TenantId tenantId, String profileName);

    AssetProfile createDefaultAssetProfile(TenantId tenantId);

    AssetProfile findDefaultAssetProfile(TenantId tenantId);

    AssetProfileInfo findDefaultAssetProfileInfo(TenantId tenantId);

    boolean setDefaultAssetProfile(TenantId tenantId, AssetProfileId assetProfileId);

    void deleteAssetProfilesByTenantId(TenantId tenantId);

    List<EntityInfo> findAssetProfileNamesByTenantId(TenantId tenantId, boolean activeOnly);

}
