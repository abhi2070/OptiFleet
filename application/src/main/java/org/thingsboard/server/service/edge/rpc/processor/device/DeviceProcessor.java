
package org.thingsboard.server.service.edge.rpc.processor.device;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.DeviceCredentialsUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DeviceProfileUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DeviceRpcCallMsg;
import org.thingsboard.server.gen.edge.v1.DeviceUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.service.edge.rpc.processor.EdgeProcessor;

public interface DeviceProcessor extends EdgeProcessor {

    ListenableFuture<Void> processDeviceMsgFromEdge(TenantId tenantId, Edge edge, DeviceUpdateMsg deviceUpdateMsg);

    ListenableFuture<Void> processDeviceCredentialsMsgFromEdge(TenantId tenantId, EdgeId edgeId, DeviceCredentialsUpdateMsg deviceCredentialsUpdateMsg);

    DownlinkMsg convertDeviceEventToDownlink(EdgeEvent edgeEvent, EdgeId edgeId, EdgeVersion edgeVersion);

    ListenableFuture<Void> processDeviceRpcCallFromEdge(TenantId tenantId, Edge edge, DeviceRpcCallMsg deviceRpcCallMsg);
}
