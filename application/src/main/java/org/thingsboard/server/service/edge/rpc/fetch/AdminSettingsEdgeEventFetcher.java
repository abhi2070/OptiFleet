
package org.thingsboard.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.edge.EdgeEventType;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.settings.AdminSettingsService;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class AdminSettingsEdgeEventFetcher implements EdgeEventFetcher {

    private final AdminSettingsService adminSettingsService;

    @Override
    public PageLink getPageLink(int pageSize) {
        return null;
    }

    public PageData<EdgeEvent> fetchEdgeEvents(TenantId tenantId, Edge edge, PageLink pageLink) {
        List<EdgeEvent> result = fetchAdminSettingsForKeys(tenantId, edge.getId(), List.of("general", "mail", "connectivity", "jwt"));

        // return PageData object to be in sync with other fetchers
        return new PageData<>(result, 1, result.size(), false);
    }

    private List<EdgeEvent> fetchAdminSettingsForKeys(TenantId tenantId, EdgeId edgeId, List<String> keys) {
        List<EdgeEvent> result = new ArrayList<>();
        for (String key : keys) {
            AdminSettings adminSettings = adminSettingsService.findAdminSettingsByKey(TenantId.SYS_TENANT_ID, key);
            if (adminSettings != null) {
                result.add(EdgeUtils.constructEdgeEvent(tenantId, edgeId, EdgeEventType.ADMIN_SETTINGS,
                        EdgeEventActionType.UPDATED, null, JacksonUtil.valueToTree(adminSettings)));
            }
        }
        return result;
    }
}
