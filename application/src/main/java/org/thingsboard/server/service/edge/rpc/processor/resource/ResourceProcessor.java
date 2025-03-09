
package org.thingsboard.server.service.edge.rpc.processor.resource;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.ResourceUpdateMsg;
import org.thingsboard.server.service.edge.rpc.processor.EdgeProcessor;

public interface ResourceProcessor extends EdgeProcessor {

    ListenableFuture<Void> processResourceMsgFromEdge(TenantId tenantId, Edge edge, ResourceUpdateMsg resourceUpdateMsg);

    DownlinkMsg convertResourceEventToDownlink(EdgeEvent edgeEvent, EdgeVersion edgeVersion);
}
