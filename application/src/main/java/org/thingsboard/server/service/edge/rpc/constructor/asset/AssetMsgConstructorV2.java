
package org.thingsboard.server.service.edge.rpc.constructor.asset;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.gen.edge.v1.AssetProfileUpdateMsg;
import org.thingsboard.server.gen.edge.v1.AssetUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class AssetMsgConstructorV2 extends BaseAssetMsgConstructor {

    @Override
    public AssetUpdateMsg constructAssetUpdatedMsg(UpdateMsgType msgType, Asset asset) {
        return AssetUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(asset))
                .setIdMSB(asset.getUuidId().getMostSignificantBits())
                .setIdLSB(asset.getUuidId().getLeastSignificantBits()).build();
    }

    @Override
    public AssetProfileUpdateMsg constructAssetProfileUpdatedMsg(UpdateMsgType msgType, AssetProfile assetProfile) {
        return AssetProfileUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(assetProfile))
                .setIdMSB(assetProfile.getId().getId().getMostSignificantBits())
                .setIdLSB(assetProfile.getId().getId().getLeastSignificantBits()).build();
    }
}
