
package org.thingsboard.server.service.entity.asset.profile;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.service.entity.SimpleTbEntityService;

public interface TbAssetProfileService extends SimpleTbEntityService<AssetProfile> {

    AssetProfile setDefaultAssetProfile(AssetProfile assetProfile, AssetProfile previousDefaultAssetProfile, User user) throws ThingsboardException;
}
