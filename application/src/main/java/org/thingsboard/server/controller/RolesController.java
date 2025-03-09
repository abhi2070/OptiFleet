package org.thingsboard.server.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.roles.RolesInfo;
import org.thingsboard.server.common.data.roles.RolesSearchQuery;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportRequest;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportResult;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.roles.TbRolesService;
import org.thingsboard.server.service.roles.RolesBulkImportService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import static org.thingsboard.server.controller.ControllerConstants.*;


@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class RolesController extends BaseController {

    private final RolesBulkImportService rolesBulkImportService;
    private final TbRolesService tbRolesService;

    public static final String ROLES_ID = "rolesId";

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/roles{rolesId}", method = RequestMethod.GET)
    @ResponseBody
    public Roles getRolesById(@ApiParam(value = ROLES_ID_PARAM_DESCRIPTION) @PathVariable(ROLES_ID) String strRolesId) throws ThingsboardException {
        checkParameter(ROLES_ID, strRolesId);
        RolesId rolesId = new RolesId(toUUID(strRolesId));
        return checkRolesId(rolesId, Operation.READ);
    }

    @ApiOperation(value = "Get Roles Info (getAssetInfoById)",
            notes = "Fetch the Roles Info object based on the provided Roles Id. " +
                    "If the user has the authority of 'Tenant Administrator', the server checks that the roles is owned by the same tenant. " +
                    "If the user has the authority of 'Customer User', the server checks that the roles is assigned to the same customer. "+
                    ROLES_INFO_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/roles/info/{rolesId}", method = RequestMethod.GET)
    @ResponseBody
    public RolesInfo getRolesInfoById(@ApiParam(value = ROLES_ID_PARAM_DESCRIPTION)
                                      @PathVariable(ROLES_ID) String strRolesId) throws ThingsboardException {
        checkParameter(ROLES_ID, strRolesId);
        RolesId rolesId = new RolesId(toUUID(strRolesId));
        return checkRolesInfoId(rolesId, Operation.READ);
    }

    @ApiOperation(value = "Create Or Update Roles (saveRoles)",
            notes = "Creates or Updates the ROles. When creating roles, platform generates Roles Id as " + UUID_WIKI_LINK +
                    "The newly created Roles id will be present in the response. " +
                    "Specify existing Roles id to update the roles. " +
                    "Referencing non-existing Roles Id will cause 'Not Found' error. " +
                    "Remove 'id', 'tenantId' and optionally 'customerId' from the request body example (below) to create new Roles entity. "
                    + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/role", method = RequestMethod.POST)
    @ResponseBody
    public Roles saveRole(@ApiParam(value = "A JSON value representing the roles.") @RequestBody Roles roles) throws Exception {
        roles.setTenantId(getTenantId());
        checkEntity(roles.getId(), roles, Resource.ROLES);
        return tbRolesService.save(roles, getCurrentUser());
    }


    @ApiOperation(value = "Delete role (deleteRole)",
            notes = "Deletes the role and all the relations (from and to the role). Referencing non-existing role Id will cause an error." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/roles/{rolesId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteRole(@ApiParam(value = ROLES_ID_PARAM_DESCRIPTION) @PathVariable(ROLES_ID) String strRolesId) throws Exception {
        checkParameter(ROLES_ID, strRolesId);
        RolesId rolesId = new RolesId(toUUID(strRolesId));
        Roles role = checkRolesId(rolesId, Operation.DELETE);
        tbRolesService.delete(role, getCurrentUser());
    }





    @ApiOperation(value = "Get Tenant Roles (getTenantRoles)", notes = "Returns a page of roles owned by tenant. " + PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/roles", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Roles> getTenantRoles(@ApiParam(value = PAGE_SIZE_DESCRIPTION) @RequestParam int pageSize, @ApiParam(value = PAGE_NUMBER_DESCRIPTION) @RequestParam int page, @ApiParam(value = ROLES_TYPE_DESCRIPTION) @RequestParam(required = false) String type, @ApiParam(value = ROLES_TEXT_SEARCH_DESCRIPTION) @RequestParam(required = false) String textSearch, @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = ROLES_SORT_PROPERTY_ALLOWABLE_VALUES) @RequestParam(required = false) String sortProperty, @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES) @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        if (type != null && type.trim().length() > 0) {
            return checkNotNull(rolesService.findRolesByTenantIdAndType(tenantId, type, pageLink));
        } else {
            return checkNotNull(rolesService.findRolesByTenantId(tenantId, pageLink));
        }
    }

    @ApiOperation(value = "Get Tenant Roles (getTenantRoles)", notes = "Requested roles must be owned by tenant that the user belongs to. " + "Roles name is an unique property of roles. So it can be used to identify the roles." + TENANT_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/roles", params = {"rolesName"}, method = RequestMethod.GET)
    @ResponseBody
    public Roles getTenantRoles(@ApiParam(value = ROLES_NAME_DESCRIPTION) @RequestParam String rolesName) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        return checkNotNull(rolesService.findRolesByTenantIdAndName(tenantId, rolesName));
    }

    @ApiOperation(value = "Find related roles (findByQuery)", notes = "Returns all roles that are related to the specific entity. " + "The entity id, relation type, role types, depth of the search, and other query parameters defined using complex 'RolesSearchQuery' object. " + "See 'Model' tab of the Parameters for more info.", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/roles", method = RequestMethod.POST)
    @ResponseBody
    public List<Roles> findByQuery(@RequestBody RolesSearchQuery query) throws ThingsboardException, ExecutionException, InterruptedException {
        checkNotNull(query);
        checkNotNull(query.getParameters());
        checkNotNull(query.getRolesTypes());
        checkEntityId(query.getParameters().getEntityId(), Operation.READ);
        List<Roles> roles = checkNotNull(rolesService.findRolesByQuery(getTenantId(), query).get());
        roles = roles.stream().filter(role -> {
            try {
                accessControlService.checkPermission(getCurrentUser(), Resource.ROLES, Operation.READ, role.getId(), role);
                return true;
            } catch (ThingsboardException e) {
                return false;
            }
        }).collect(Collectors.toList());
        return roles;
    }

    @ApiOperation(value = "Import the bulk of roles (processRolesBulkImport)", notes = "There's an ability to import the bulk of roles using the only .csv file.", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @PostMapping("/roles/bulk_import")
    public BulkImportResult<Roles> processRolesBulkImport(@RequestBody BulkImportRequest request) throws Exception {
        SecurityUser user = getCurrentUser();
        return rolesBulkImportService.processBulkImport(request, user);
    }

}
