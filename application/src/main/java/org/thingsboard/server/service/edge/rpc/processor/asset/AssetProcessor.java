
package org.thingsboard.server.service.edge.rpc.processor.asset;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.AssetUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.service.edge.rpc.processor.EdgeProcessor;

public interface AssetProcessor extends EdgeProcessor {

    ListenableFuture<Void> processAssetMsgFromEdge(TenantId tenantId, Edge edge, AssetUpdateMsg assetUpdateMsg);

    DownlinkMsg convertAssetEventToDownlink(EdgeEvent edgeEvent, EdgeId edgeId, EdgeVersion edgeVersion);
}
