
package org.thingsboard.server.service.edge.rpc.constructor.tenant;

import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.TenantProfileUpdateMsg;
import org.thingsboard.server.gen.edge.v1.TenantUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface TenantMsgConstructor extends MsgConstructor {

    TenantUpdateMsg constructTenantUpdateMsg(UpdateMsgType msgType, Tenant tenant);

    TenantProfileUpdateMsg constructTenantProfileUpdateMsg(UpdateMsgType msgType, TenantProfile tenantProfile, EdgeVersion edgeVersion);
}
