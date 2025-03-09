package org.thingsboard.server.service.edge.rpc.processor.roles;


import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.RolesId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.gen.edge.v1.AssetUpdateMsg;
import org.thingsboard.server.gen.edge.v1.RolesUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Primary
@Component
@TbCoreComponent
public class RolesEdgeProcessorV2 extends RolesEdgeProcessor {


    @Override
    protected Roles constructRolesFromUpdateMsg(TenantId tenantId, RolesId rolesId, RolesUpdateMsg rolesUpdateMsg) {

        return JacksonUtil.fromString(rolesUpdateMsg.getEntity(), Roles.class, true);
    }

    @Override
    protected void setCustomerId(TenantId tenantId, CustomerId customerId, Roles roles, RolesUpdateMsg rolesUpdateMsg) {
        CustomerId customerUUID = roles.getCustomerId() != null ? roles.getCustomerId() : customerId;
        roles.setCustomerId(customerUUID);
    }
}
