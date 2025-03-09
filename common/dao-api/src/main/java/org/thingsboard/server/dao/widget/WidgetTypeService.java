
package org.thingsboard.server.dao.widget;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.DeprecatedFilter;
import org.thingsboard.server.common.data.widget.WidgetType;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetTypeInfo;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

public interface WidgetTypeService extends EntityDaoService {

    WidgetType findWidgetTypeById(TenantId tenantId, WidgetTypeId widgetTypeId);

    WidgetTypeDetails findWidgetTypeDetailsById(TenantId tenantId, WidgetTypeId widgetTypeId);

    boolean widgetTypeExistsByTenantIdAndWidgetTypeId(TenantId tenantId, WidgetTypeId widgetTypeId);

    WidgetTypeDetails saveWidgetType(WidgetTypeDetails widgetType);

    void deleteWidgetType(TenantId tenantId, WidgetTypeId widgetTypeId);

    PageData<WidgetTypeInfo> findSystemWidgetTypesByPageLink(TenantId tenantId, boolean fullSearch, DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink);

    PageData<WidgetTypeInfo> findAllTenantWidgetTypesByTenantIdAndPageLink(TenantId tenantId, boolean fullSearch, DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink);

    PageData<WidgetTypeInfo> findTenantWidgetTypesByTenantIdAndPageLink(TenantId tenantId, boolean fullSearch, DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink);

    List<WidgetType> findWidgetTypesByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId);

    List<WidgetTypeDetails> findWidgetTypesDetailsByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId);

    PageData<WidgetTypeInfo> findWidgetTypesInfosByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId, boolean fullSearch, DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink);

    List<String> findWidgetFqnsByWidgetsBundleId(TenantId tenantId, WidgetsBundleId widgetsBundleId);

    WidgetType findWidgetTypeByTenantIdAndFqn(TenantId tenantId, String fqn);

    void updateWidgetsBundleWidgetTypes(TenantId tenantId, WidgetsBundleId widgetsBundleId, List<WidgetTypeId> widgetTypeIds);

    void updateWidgetsBundleWidgetFqns(TenantId tenantId, WidgetsBundleId widgetsBundleId, List<String> widgetFqns);

    void deleteWidgetTypesByTenantId(TenantId tenantId);

}
