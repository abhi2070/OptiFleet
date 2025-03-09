package org.thingsboard.server.service.edge.rpc.constructor.integration;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.gen.edge.v1.IntegrationUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class IntegrationMsgConstructorV2 extends BaseIntegrationMsgConstructor {

    @Override
    public IntegrationUpdateMsg constructIntegrationUpdatedMsg(UpdateMsgType msgType, Integration integration) {
        return IntegrationUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(integration))
                .setIdMSB(integration.getUuidId().getMostSignificantBits())
                .setIdLSB(integration.getUuidId().getLeastSignificantBits()).build();
    }
}
