
package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.dao.resource.ImageService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.widgets.bundle.TbWidgetsBundleService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.thingsboard.server.controller.ControllerConstants.AVAILABLE_FOR_ANY_AUTHORIZED_USER;
import static org.thingsboard.server.controller.ControllerConstants.INLINE_IMAGES;
import static org.thingsboard.server.controller.ControllerConstants.INLINE_IMAGES_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_DATA_PARAMETERS;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_NUMBER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_SIZE_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_ALLOWABLE_VALUES;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_PROPERTY_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH;
import static org.thingsboard.server.controller.ControllerConstants.UUID_WIKI_LINK;
import static org.thingsboard.server.controller.ControllerConstants.WIDGET_BUNDLE_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.WIDGET_BUNDLE_SORT_PROPERTY_ALLOWABLE_VALUES;
import static org.thingsboard.server.controller.ControllerConstants.WIDGET_BUNDLE_TEXT_SEARCH_DESCRIPTION;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
public class WidgetsBundleController extends BaseController {

    private final TbWidgetsBundleService tbWidgetsBundleService;
    private final ImageService imageService;

    private static final String WIDGET_BUNDLE_DESCRIPTION = "Widget Bundle represents a group(bundle) of widgets. Widgets are grouped into bundle by type or use case. ";
    private static final String FULL_SEARCH_PARAM_DESCRIPTION = "Optional boolean parameter indicating extended search of widget bundles by description and by name / description of related widget types";
    private static final String TENANT_BUNDLES_ONLY_DESCRIPTION = "Optional boolean parameter to include only tenant-level bundles without system";

    @ApiOperation(value = "Get Widget Bundle (getWidgetsBundleById)",
            notes = "Get the Widget Bundle based on the provided Widget Bundle Id. " + WIDGET_BUNDLE_DESCRIPTION + AVAILABLE_FOR_ANY_AUTHORIZED_USER)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/widgetsBundle/{widgetsBundleId}", method = RequestMethod.GET)
    @ResponseBody
    public WidgetsBundle getWidgetsBundleById(
            @ApiParam(value = WIDGET_BUNDLE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable("widgetsBundleId") String strWidgetsBundleId,
            @ApiParam(value = INLINE_IMAGES_DESCRIPTION)
            @RequestParam(value = INLINE_IMAGES, required = false) boolean inlineImages) throws ThingsboardException {
        checkParameter("widgetsBundleId", strWidgetsBundleId);
        WidgetsBundleId widgetsBundleId = new WidgetsBundleId(toUUID(strWidgetsBundleId));
        var result = checkWidgetsBundleId(widgetsBundleId, Operation.READ);
        if (inlineImages) {
            imageService.inlineImage(result);
        }
        return result;
    }

    @ApiOperation(value = "Create Or Update Widget Bundle (saveWidgetsBundle)",
            notes = "Create or update the Widget Bundle. " + WIDGET_BUNDLE_DESCRIPTION + " " +
                    "When creating the bundle, platform generates Widget Bundle Id as " + UUID_WIKI_LINK +
                    "The newly created Widget Bundle Id will be present in the response. " +
                    "Specify existing Widget Bundle id to update the Widget Bundle. " +
                    "Referencing non-existing Widget Bundle Id will cause 'Not Found' error." +
                    "\n\nWidget Bundle alias is unique in the scope of tenant. " +
                    "Special Tenant Id '13814000-1dd2-11b2-8080-808080808080' is automatically used if the create bundle request is sent by user with 'SYS_ADMIN' authority." +
                    "Remove 'id', 'tenantId' from the request body example (below) to create new Widgets Bundle entity." +
                    SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/widgetsBundle", method = RequestMethod.POST)
    @ResponseBody
    public WidgetsBundle saveWidgetsBundle(
            @ApiParam(value = "A JSON value representing the Widget Bundle.", required = true)
            @RequestBody WidgetsBundle widgetsBundle) throws Exception {
        var currentUser = getCurrentUser();
        if (Authority.SYS_ADMIN.equals(currentUser.getAuthority())) {
            widgetsBundle.setTenantId(TenantId.SYS_TENANT_ID);
        } else {
            widgetsBundle.setTenantId(currentUser.getTenantId());
        }

        checkEntity(widgetsBundle.getId(), widgetsBundle, Resource.WIDGETS_BUNDLE);

        return tbWidgetsBundleService.save(widgetsBundle, currentUser);
    }

    @ApiOperation(value = "Update widgets bundle widgets types list (updateWidgetsBundleWidgetTypes)",
            notes = "Updates widgets bundle widgets list." + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/widgetsBundle/{widgetsBundleId}/widgetTypes", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void updateWidgetsBundleWidgetTypes(
            @ApiParam(value = WIDGET_BUNDLE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable("widgetsBundleId") String strWidgetsBundleId,
            @ApiParam(value = "Ordered list of widget type Ids to be included by widgets bundle")
            @RequestBody List<String> strWidgetTypeIds) throws Exception {
        checkParameter("widgetsBundleId", strWidgetsBundleId);
        WidgetsBundleId widgetsBundleId = new WidgetsBundleId(toUUID(strWidgetsBundleId));
        checkNotNull(strWidgetTypeIds);
        Set<WidgetTypeId> widgetTypeIds = new LinkedHashSet<>();
        var currentUser = getCurrentUser();
        TenantId tenantId = currentUser.getTenantId();
        for (String strWidgetTypeId : strWidgetTypeIds) {
            WidgetTypeId widgetTypeId = new WidgetTypeId(toUUID(strWidgetTypeId));
            if (!widgetTypeIds.contains(widgetTypeId) &&
                    widgetTypeService.widgetTypeExistsByTenantIdAndWidgetTypeId(tenantId, widgetTypeId)) {
                widgetTypeIds.add(widgetTypeId);
            }
        }
        tbWidgetsBundleService.updateWidgetsBundleWidgetTypes(widgetsBundleId, new ArrayList<>(widgetTypeIds), currentUser);
    }

    @ApiOperation(value = "Update widgets bundle widgets list from widget type FQNs list (updateWidgetsBundleWidgetFqns)",
            notes = "Updates widgets bundle widgets list from widget type FQNs list." + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/widgetsBundle/{widgetsBundleId}/widgetTypeFqns", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void updateWidgetsBundleWidgetFqns(
            @ApiParam(value = WIDGET_BUNDLE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable("widgetsBundleId") String strWidgetsBundleId,
            @ApiParam(value = "Ordered list of widget type FQNs to be included by widgets bundle")
            @RequestBody List<String> widgetTypeFqns) throws Exception {
        checkParameter("widgetsBundleId", strWidgetsBundleId);
        WidgetsBundleId widgetsBundleId = new WidgetsBundleId(toUUID(strWidgetsBundleId));
        checkNotNull(widgetTypeFqns);
        var currentUser = getCurrentUser();
        tbWidgetsBundleService.updateWidgetsBundleWidgetFqns(widgetsBundleId, widgetTypeFqns, currentUser);
    }

    @ApiOperation(value = "Delete widgets bundle (deleteWidgetsBundle)",
            notes = "Deletes the widget bundle. Referencing non-existing Widget Bundle Id will cause an error." + SYSTEM_OR_TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/widgetsBundle/{widgetsBundleId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteWidgetsBundle(
            @ApiParam(value = WIDGET_BUNDLE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable("widgetsBundleId") String strWidgetsBundleId) throws ThingsboardException {
        checkParameter("widgetsBundleId", strWidgetsBundleId);
        WidgetsBundleId widgetsBundleId = new WidgetsBundleId(toUUID(strWidgetsBundleId));
        WidgetsBundle widgetsBundle = checkWidgetsBundleId(widgetsBundleId, Operation.DELETE);
        tbWidgetsBundleService.delete(widgetsBundle, getCurrentUser());
    }

    @ApiOperation(value = "Get Widget Bundles (getWidgetsBundles)",
            notes = "Returns a page of Widget Bundle objects available for current user. " + WIDGET_BUNDLE_DESCRIPTION + " " +
                    PAGE_DATA_PARAMETERS + AVAILABLE_FOR_ANY_AUTHORIZED_USER)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/widgetsBundles", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<WidgetsBundle> getWidgetsBundles(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = WIDGET_BUNDLE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = WIDGET_BUNDLE_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder,
            @ApiParam(value = TENANT_BUNDLES_ONLY_DESCRIPTION)
            @RequestParam(required = false) Boolean tenantOnly,
            @ApiParam(value = FULL_SEARCH_PARAM_DESCRIPTION)
            @RequestParam(required = false) Boolean fullSearch) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        if (Authority.SYS_ADMIN.equals(getCurrentUser().getAuthority())) {
            return checkNotNull(widgetsBundleService.findSystemWidgetsBundlesByPageLink(getTenantId(), fullSearch != null && fullSearch, pageLink));
        } else {
            TenantId tenantId = getCurrentUser().getTenantId();
            if (tenantOnly != null && tenantOnly) {
                return checkNotNull(widgetsBundleService.findTenantWidgetsBundlesByTenantIdAndPageLink(tenantId, fullSearch != null && fullSearch, pageLink));
            } else {
                return checkNotNull(widgetsBundleService.findAllTenantWidgetsBundlesByTenantIdAndPageLink(tenantId, fullSearch != null && fullSearch, pageLink));
            }
        }
    }

    @ApiOperation(value = "Get all Widget Bundles (getWidgetsBundles)",
            notes = "Returns an array of Widget Bundle objects that are available for current user." + WIDGET_BUNDLE_DESCRIPTION + " " + AVAILABLE_FOR_ANY_AUTHORIZED_USER)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/widgetsBundles", method = RequestMethod.GET)
    @ResponseBody
    public List<WidgetsBundle> getWidgetsBundles() throws ThingsboardException {
        if (Authority.SYS_ADMIN.equals(getCurrentUser().getAuthority())) {
            return checkNotNull(widgetsBundleService.findSystemWidgetsBundles(getTenantId()));
        } else {
            TenantId tenantId = getCurrentUser().getTenantId();
            return checkNotNull(widgetsBundleService.findAllTenantWidgetsBundlesByTenantId(tenantId));
        }
    }

}
