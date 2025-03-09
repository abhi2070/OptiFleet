package org.thingsboard.server.service.edge.rpc.constructor.integration;

import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.gen.edge.v1.IntegrationUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface IntegrationMsgConstructor extends MsgConstructor {

    IntegrationUpdateMsg constructIntegrationUpdatedMsg(UpdateMsgType msgType, Integration integration);

    IntegrationUpdateMsg constructIntegrationDeleteMsg(IntegrationId integrationId);
}
