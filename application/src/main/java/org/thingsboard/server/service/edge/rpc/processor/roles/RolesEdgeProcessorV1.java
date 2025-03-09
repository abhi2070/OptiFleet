package org.thingsboard.server.service.edge.rpc.processor.roles;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.gen.edge.v1.AssetUpdateMsg;
import org.thingsboard.server.gen.edge.v1.RolesUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class RolesEdgeProcessorV1 extends RolesEdgeProcessor {

    @Override
    protected Roles constructRolesFromUpdateMsg(TenantId tenantId, RolesId rolesId, RolesUpdateMsg rolesUpdateMsg) {
        Roles roles = new Roles();
        roles.setTenantId(tenantId);
        roles.setName(rolesUpdateMsg.getName());
        roles.setCreatedTime(Uuids.unixTimestamp(rolesId.getId()));
        roles.setType(rolesUpdateMsg.getType());
        roles.setAdditionalInfo(rolesUpdateMsg.hasAdditionalInfo()
                ? JacksonUtil.toJsonNode(rolesUpdateMsg.getAdditionalInfo()) : null);

        CustomerId customerId = safeGetCustomerId(rolesUpdateMsg.getCustomerIdMSB(), rolesUpdateMsg.getCustomerIdLSB());
        roles.setCustomerId(customerId);

        return roles;
    }
    @Override
    protected void setCustomerId(TenantId tenantId, CustomerId customerId, Roles roles, RolesUpdateMsg rolesUpdateMsg) {
        CustomerId customerUUID = safeGetCustomerId(rolesUpdateMsg.getCustomerIdMSB(), rolesUpdateMsg.getCustomerIdLSB());
        roles.setCustomerId(customerUUID != null ? customerUUID : customerId);
    }

}
