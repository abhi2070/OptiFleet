package org.thingsboard.server.service.sync.ie.importing.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.RolesId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.dao.roles.RolesService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class RolesImportService extends BaseEntityImportService<RolesId, Roles, EntityExportData<Roles>> {

    private final RolesService rolesService;

    @Override
    protected void setOwner(TenantId tenantId, Roles roles, IdProvider idProvider) {
        roles.setTenantId(tenantId);
    }

    @Override
    protected Roles prepare(EntitiesImportCtx ctx, Roles roles, Roles old, EntityExportData<Roles> exportData, IdProvider idProvider) {
        return roles;
    }

    @Override
    protected Roles saveOrUpdate(EntitiesImportCtx ctx, Roles roles, EntityExportData<Roles> exportData, IdProvider idProvider) {
        return rolesService.saveRoles(roles);
    }

    @Override
    protected Roles deepCopy(Roles roles) {
        return new Roles(roles);
    }

    @Override
    protected void cleanupForComparison(Roles e) {
        super.cleanupForComparison(e);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ROLES;
    }
}
