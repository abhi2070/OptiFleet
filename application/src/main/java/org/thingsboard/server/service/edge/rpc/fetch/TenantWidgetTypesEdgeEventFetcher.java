
package org.thingsboard.server.service.edge.rpc.fetch;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.DeprecatedFilter;
import org.thingsboard.server.common.data.widget.WidgetTypeInfo;
import org.thingsboard.server.dao.widget.WidgetTypeService;

@Slf4j
public class TenantWidgetTypesEdgeEventFetcher extends BaseWidgetTypesEdgeEventFetcher {

    public TenantWidgetTypesEdgeEventFetcher(WidgetTypeService widgetTypeService) {
        super(widgetTypeService);
    }
    @Override
    protected PageData<WidgetTypeInfo> findWidgetTypes(TenantId tenantId, PageLink pageLink) {
        return widgetTypeService.findTenantWidgetTypesByTenantIdAndPageLink(tenantId, false, DeprecatedFilter.ALL, null, pageLink);
    }
}
