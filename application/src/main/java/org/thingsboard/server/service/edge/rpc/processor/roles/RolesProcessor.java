package org.thingsboard.server.service.edge.rpc.processor.roles;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.RolesUpdateMsg;
import org.thingsboard.server.service.edge.rpc.processor.EdgeProcessor;

public interface RolesProcessor  extends EdgeProcessor {

    ListenableFuture<Void> processRolesMsgFromEdge(TenantId tenantId, Edge edge, RolesUpdateMsg rolesUpdateMsg);

    DownlinkMsg convertRolesEventToDownlink(EdgeEvent edgeEvent, EdgeId edgeId, EdgeVersion edgeVersion);
}
