package org.thingsboard.server.service.edge.rpc.processor.integration;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.IntegrationUpdateMsg;
import org.thingsboard.server.service.edge.rpc.processor.EdgeProcessor;

public interface IntegrationProcessor extends EdgeProcessor {

    ListenableFuture<Void> processIntegrationMsgFromEdge(TenantId tenantId, Edge edge, IntegrationUpdateMsg integrationUpdateMsg);

    DownlinkMsg convertIntegrationEventToDownlink(EdgeEvent edgeEvent, EdgeId edgeId, EdgeVersion edgeVersion);

}
