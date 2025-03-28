
package org.thingsboard.server.service.sync.ie.importing.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.ResourceType;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.dao.resource.ImageService;
import org.thingsboard.server.dao.resource.ResourceService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class ResourceImportService extends BaseEntityImportService<TbResourceId, TbResource, EntityExportData<TbResource>> {

    private final ResourceService resourceService;
    private final ImageService imageService;

    @Override
    protected void setOwner(TenantId tenantId, TbResource resource, IdProvider idProvider) {
        resource.setTenantId(tenantId);
    }

    @Override
    protected TbResource prepare(EntitiesImportCtx ctx, TbResource resource, TbResource oldResource, EntityExportData<TbResource> exportData, IdProvider idProvider) {
        return resource;
    }

    @Override
    protected TbResource findExistingEntity(EntitiesImportCtx ctx, TbResource resource, IdProvider idProvider) {
        TbResource existingResource = super.findExistingEntity(ctx, resource, idProvider);
        if (existingResource == null && ctx.isFindExistingByName()) {
            existingResource = resourceService.findResourceByTenantIdAndKey(ctx.getTenantId(), resource.getResourceType(), resource.getResourceKey());
        }
        return existingResource;
    }

    @Override
    protected boolean compare(EntitiesImportCtx ctx, EntityExportData<TbResource> exportData, TbResource prepared, TbResource existing) {
        return true;
    }

    @Override
    protected TbResource deepCopy(TbResource resource) {
        return new TbResource(resource);
    }

    @Override
    protected TbResource saveOrUpdate(EntitiesImportCtx ctx, TbResource resource, EntityExportData<TbResource> exportData, IdProvider idProvider) {
        if (resource.getResourceType() == ResourceType.IMAGE) {
            return new TbResource(imageService.saveImage(resource));
        } else {
            resource = resourceService.saveResource(resource);
            resource.setData(null);
            resource.setPreview(null);
            return resource;
        }
    }

    @Override
    protected void onEntitySaved(User user, TbResource savedResource, TbResource oldResource) throws ThingsboardException {
        super.onEntitySaved(user, savedResource, oldResource);
        clusterService.onResourceChange(savedResource, null);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.TB_RESOURCE;
    }

}
