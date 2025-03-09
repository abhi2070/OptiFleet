
package org.thingsboard.server.service.edge.rpc.processor.asset.profile;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.AssetProfileUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.service.edge.rpc.processor.EdgeProcessor;

public interface AssetProfileProcessor extends EdgeProcessor {

    ListenableFuture<Void> processAssetProfileMsgFromEdge(TenantId tenantId, Edge edge, AssetProfileUpdateMsg assetProfileUpdateMsg);

    DownlinkMsg convertAssetProfileEventToDownlink(EdgeEvent edgeEvent, EdgeId edgeId, EdgeVersion edgeVersion);
}
