package org.thingsboard.server.dao.roles;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.EntitySubtype;

import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.roles.RolesInfo;
import org.thingsboard.server.common.data.roles.RolesSearchQuery;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.UUID;


public interface RolesService extends EntityDaoService {


    RolesInfo findRolesInfoById(TenantId tenantId, RolesId rolesId);

    Roles findRolesById(TenantId tenantId, RolesId rolesId);


    ListenableFuture<Roles> findRolesByIdAsync(TenantId tenantId, RolesId rolesId);

    Roles findRolesByTenantIdAndName(TenantId tenantId, String name);

    Roles saveRoles(Roles roles, boolean doValidate);

    Roles saveRoles(Roles roles);


    void deleteRoles(TenantId tenantId, RolesId rolesId);

    PageData<Roles> findRolesByTenantId(TenantId tenantId, PageLink pageLink);


    PageData<Roles> findRolesByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    Roles assignRolesToEdge(TenantId tenantId, RolesId rolesId, EdgeId edgeId);

    Roles unassignRolesFromEdge(TenantId tenantId, RolesId rolesId, EdgeId edgeId);

    ListenableFuture<List<Roles>> findRolesByQuery(TenantId tenantId, RolesSearchQuery query);

}

