
package org.thingsboard.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.edge.EdgeEventType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetTypeInfo;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.dao.widget.WidgetTypeService;
import org.thingsboard.server.dao.widget.WidgetsBundleService;

@Slf4j
@AllArgsConstructor
public abstract class BaseWidgetTypesEdgeEventFetcher extends BasePageableEdgeEventFetcher<WidgetTypeInfo> {

    protected final WidgetTypeService widgetTypeService;

    @Override
    PageData<WidgetTypeInfo> fetchPageData(TenantId tenantId, Edge edge, PageLink pageLink) {
        return findWidgetTypes(tenantId, pageLink);
    }

    @Override
    EdgeEvent constructEdgeEvent(TenantId tenantId, Edge edge, WidgetTypeInfo widgetTypeInfo) {
        return EdgeUtils.constructEdgeEvent(tenantId, edge.getId(), EdgeEventType.WIDGET_TYPE,
                EdgeEventActionType.ADDED, widgetTypeInfo.getId(), null);
    }

    protected abstract PageData<WidgetTypeInfo> findWidgetTypes(TenantId tenantId, PageLink pageLink);
}
