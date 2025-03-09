
package org.thingsboard.server.service.edge.rpc.constructor.asset;

import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.AssetProfileId;
import org.thingsboard.server.gen.edge.v1.AssetProfileUpdateMsg;
import org.thingsboard.server.gen.edge.v1.AssetUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface AssetMsgConstructor extends MsgConstructor {

    AssetUpdateMsg constructAssetUpdatedMsg(UpdateMsgType msgType, Asset asset);

    AssetUpdateMsg constructAssetDeleteMsg(AssetId assetId);

    AssetProfileUpdateMsg constructAssetProfileUpdatedMsg(UpdateMsgType msgType, AssetProfile assetProfile);

    AssetProfileUpdateMsg constructAssetProfileDeleteMsg(AssetProfileId assetProfileId);
}
