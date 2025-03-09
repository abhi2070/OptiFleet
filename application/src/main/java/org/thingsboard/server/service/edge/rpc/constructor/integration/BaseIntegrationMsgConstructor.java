package org.thingsboard.server.service.edge.rpc.constructor.integration;

import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.gen.edge.v1.IntegrationUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;

public abstract class BaseIntegrationMsgConstructor implements IntegrationMsgConstructor{

    @Override
    public IntegrationUpdateMsg constructIntegrationDeleteMsg(IntegrationId integrationId) {
        return IntegrationUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(integrationId.getId().getMostSignificantBits())
                .setIdLSB(integrationId.getId().getLeastSignificantBits()).build();
    }

//    @Override
//    public AssetProfileUpdateMsg constructAssetProfileDeleteMsg(AssetProfileId assetProfileId) {
//        return AssetProfileUpdateMsg.newBuilder()
//                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
//                .setIdMSB(assetProfileId.getId().getMostSignificantBits())
//                .setIdLSB(assetProfileId.getId().getLeastSignificantBits()).build();
//    }
}
