
package org.thingsboard.server.service.entity.widgets.bundle;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.dao.widget.WidgetTypeService;
import org.thingsboard.server.dao.widget.WidgetsBundleService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.AbstractTbEntityService;

import java.util.List;

@Service
@TbCoreComponent
@AllArgsConstructor
public class DefaultWidgetsBundleService extends AbstractTbEntityService implements TbWidgetsBundleService {

    private final WidgetsBundleService widgetsBundleService;
    private final WidgetTypeService widgetTypeService;

    @Override
    public WidgetsBundle save(WidgetsBundle widgetsBundle, User user) throws Exception {
        ActionType actionType = widgetsBundle.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = widgetsBundle.getTenantId();
        try {
            WidgetsBundle savedWidgetsBundle = checkNotNull(widgetsBundleService.saveWidgetsBundle(widgetsBundle));
            autoCommit(user, savedWidgetsBundle.getId());
            notificationEntityService.logEntityAction(tenantId, savedWidgetsBundle.getId(), savedWidgetsBundle,
                    null, actionType, user);
            return savedWidgetsBundle;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.WIDGETS_BUNDLE), widgetsBundle, actionType, user, e);
            throw e;
        }
    }

    @Override
    public void delete(WidgetsBundle widgetsBundle, User user) {
        ActionType actionType = ActionType.DELETED;
        TenantId tenantId = widgetsBundle.getTenantId();
        try {
            widgetsBundleService.deleteWidgetsBundle(widgetsBundle.getTenantId(), widgetsBundle.getId());
            notificationEntityService.logEntityAction(tenantId, widgetsBundle.getId(), widgetsBundle, null, actionType, user);
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.WIDGETS_BUNDLE), actionType, user, e, widgetsBundle.getId());
            throw e;
        }
    }

    @Override
    public void updateWidgetsBundleWidgetTypes(WidgetsBundleId widgetsBundleId, List<WidgetTypeId> widgetTypeIds, User user) throws Exception {
        widgetTypeService.updateWidgetsBundleWidgetTypes(user.getTenantId(), widgetsBundleId, widgetTypeIds);
        autoCommit(user, widgetsBundleId);
    }

    @Override
    public void updateWidgetsBundleWidgetFqns(WidgetsBundleId widgetsBundleId, List<String> widgetFqns, User user) throws Exception {
        widgetTypeService.updateWidgetsBundleWidgetFqns(user.getTenantId(), widgetsBundleId, widgetFqns);
        autoCommit(user, widgetsBundleId);
    }
}
