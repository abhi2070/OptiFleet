package org.thingsboard.server.service.edge.rpc.constructor.integration;


import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.gen.edge.v1.IntegrationUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class IntegrationMsgConstructorV1 extends BaseIntegrationMsgConstructor {

    @Override
    public IntegrationUpdateMsg constructIntegrationUpdatedMsg(UpdateMsgType msgType, Integration integration) {
        IntegrationUpdateMsg.Builder builder = IntegrationUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(integration.getUuidId().getMostSignificantBits())
                .setIdLSB(integration.getUuidId().getLeastSignificantBits())
                .setName(integration.getName())
                .setType(integration.getType());
//        if (asset.getLabel() != null) {
//            builder.setLabel(asset.getLabel());
//        }
//        if (integration.getCustomerId() != null) {
//            builder.setCustomerIdMSB(integration.getCustomerId().getId().getMostSignificantBits());
//            builder.setCustomerIdLSB(integration.getCustomerId().getId().getLeastSignificantBits());
//        }
//        if (asset.getAssetProfileId() != null) {
//            builder.setAssetProfileIdMSB(asset.getAssetProfileId().getId().getMostSignificantBits());
//            builder.setAssetProfileIdLSB(asset.getAssetProfileId().getId().getLeastSignificantBits());
//        }
//        if (asset.getAdditionalInfo() != null) {
//            builder.setAdditionalInfo(JacksonUtil.toString(asset.getAdditionalInfo()));
//        }
        return builder.build();
    }
}
