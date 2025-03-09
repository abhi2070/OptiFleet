
package org.thingsboard.server.service.sync.ie.importing.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.sync.ie.WidgetTypeExportData;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.dao.widget.WidgetTypeService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class WidgetTypeImportService extends BaseEntityImportService<WidgetTypeId, WidgetTypeDetails, WidgetTypeExportData> {

    private final WidgetTypeService widgetTypeService;

    @Override
    protected void setOwner(TenantId tenantId, WidgetTypeDetails widgetsBundle, IdProvider idProvider) {
        widgetsBundle.setTenantId(tenantId);
    }

    @Override
    protected WidgetTypeDetails prepare(EntitiesImportCtx ctx, WidgetTypeDetails widgetsBundle, WidgetTypeDetails old, WidgetTypeExportData exportData, IdProvider idProvider) {
        return widgetsBundle;
    }

    @Override
    protected WidgetTypeDetails saveOrUpdate(EntitiesImportCtx ctx, WidgetTypeDetails widgetsBundle, WidgetTypeExportData exportData, IdProvider idProvider) {
        return widgetTypeService.saveWidgetType(widgetsBundle);
    }

    @Override
    protected boolean compare(EntitiesImportCtx ctx, WidgetTypeExportData exportData, WidgetTypeDetails prepared, WidgetTypeDetails existing) {
        return true;
    }

    @Override
    protected WidgetTypeDetails deepCopy(WidgetTypeDetails widgetsBundle) {
        return new WidgetTypeDetails(widgetsBundle);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.WIDGET_TYPE;
    }

}
