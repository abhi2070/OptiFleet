package org.thingsboard.server.service.edge.rpc.processor.roles;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.id.RolesId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.AssetUpdateMsg;
import org.thingsboard.server.gen.edge.v1.RolesUpdateMsg;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;

@Slf4j
public abstract class BaseRolesProcessor  extends BaseEdgeProcessor {
    protected Pair<Boolean, Boolean> saveOrUpdateRoles(TenantId tenantId, RolesId rolesId, RolesUpdateMsg rolesUpdateMsg) {
        boolean created = false;
        boolean rolesNameUpdated = false;
        rolesCreationLock.lock();
        try {
            Roles roles = constructRolesFromUpdateMsg(tenantId, rolesId, rolesUpdateMsg);
            if (roles == null) {
                throw new RuntimeException("[{" + tenantId + "}] rolesUpdateMsg {" + rolesUpdateMsg + " } cannot be converted to roles");
            }
            Roles rolesById = rolesService.findRolesById(tenantId, rolesId);
            if (rolesById == null) {
                created = true;
                roles.setId(null);
            } else {
                roles.setId(rolesId);
            }
            String rolesName = roles.getName();
            Roles rolesByName = rolesService.findRolesByTenantIdAndName(tenantId, rolesName);
            if (rolesByName != null && !rolesByName.getId().equals(rolesId)) {
                rolesName = rolesName + "_" + StringUtils.randomAlphanumeric(15);
                log.warn("[{}] Roles with name {} already exists. Renaming roles name to {}",
                        tenantId, roles.getName(), rolesName);
                rolesNameUpdated = true;
            }
            roles.setName(rolesName);

            rolesValidator.validate(roles, Roles::getTenantId);
            if (created) {
                roles.setId(rolesId);
            }
            rolesService.saveRoles(roles, false);
        } catch (Exception e) {
            log.error("[{}] Failed to process roles update msg [{}]", tenantId, rolesUpdateMsg, e);
            throw e;
        } finally {
            rolesCreationLock.unlock();
        }
        return Pair.of(created, rolesNameUpdated);
    }

    protected abstract Roles constructRolesFromUpdateMsg(TenantId tenantId, RolesId rolesId, RolesUpdateMsg rolesUpdateMsg);

    protected abstract void setCustomerId(TenantId tenantId, CustomerId customerId, Roles roles, RolesUpdateMsg rolesUpdateMsg);

}
