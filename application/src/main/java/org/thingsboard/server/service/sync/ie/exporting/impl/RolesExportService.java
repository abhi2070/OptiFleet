package org.thingsboard.server.service.sync.ie.exporting.impl;

import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.RolesId;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesExportCtx;

import java.util.Set;

@Service
@TbCoreComponent
public class RolesExportService extends BaseEntityExportService<RolesId, Roles, EntityExportData<Roles>> {

    @Override
    protected void setRelatedEntities(EntitiesExportCtx<?> ctx, Roles roles, EntityExportData<Roles> exportData) {

    }

    @Override
    public Set<EntityType> getSupportedEntityTypes() {
        return Set.of(EntityType.ROLES);
    }
}
