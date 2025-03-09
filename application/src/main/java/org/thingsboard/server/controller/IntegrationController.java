package org.thingsboard.server.controller;

import com.google.common.util.concurrent.ListenableFuture;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.integration.IntegrationInfo;
import org.thingsboard.server.common.data.integration.IntegrationSearchQuery;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportRequest;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportResult;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.integration.TbIntegrationService;
import org.thingsboard.server.service.integration.IntegrationBulkImportService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.thingsboard.server.controller.ControllerConstants.*;
import static org.thingsboard.server.controller.EdgeController.EDGE_ID;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class IntegrationController extends BaseController{

    private final IntegrationBulkImportService integrationBulkImportService;
    private final TbIntegrationService tbIntegrationService;

    public static final String INTEGRATION_ID = "integrationId";
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/integration/{integrationId}", method = RequestMethod.GET)
    @ResponseBody
    public Integration getIntegrationById(@ApiParam(value = INTEGRATION_ID_PARAM_DESCRIPTION)
                              @PathVariable(INTEGRATION_ID) String strIntegrationId) throws ThingsboardException {
        checkParameter(INTEGRATION_ID, strIntegrationId);
        IntegrationId integrationId = new IntegrationId(toUUID(strIntegrationId));
        return checkIntegrationId(integrationId, Operation.READ);
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/integration", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public Integration saveIntegration(@ApiParam(value = "A JSON value representing the integration.") @RequestBody Integration integration) throws Exception {
        integration.setTenantId(getTenantId());
        checkEntity(integration.getId(), integration, Resource.INTEGRATION);
        return tbIntegrationService.save(integration, getCurrentUser());
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/integration/{integrationId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteIntegration(@ApiParam(value = INTEGRATION_ID_PARAM_DESCRIPTION)
                                      @PathVariable(INTEGRATION_ID) String strIntegrationId) throws Exception {
        checkParameter(INTEGRATION_ID, strIntegrationId);
        IntegrationId integrationId = new IntegrationId(toUUID(strIntegrationId));
        Integration  integration = checkIntegrationId(integrationId, Operation.DELETE);
        tbIntegrationService.delete(integration, getCurrentUser());

    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/integration/info/{integrationId}", method = RequestMethod.GET)
    @ResponseBody
    public IntegrationInfo getIntegrationInfoById(@ApiParam(value = INTEGRATION_ID_PARAM_DESCRIPTION)
                                                  @PathVariable(INTEGRATION_ID) String strIntegrationId) throws ThingsboardException {
        checkParameter(INTEGRATION_ID, strIntegrationId);
        IntegrationId integrationId = new IntegrationId(toUUID(strIntegrationId));
        return checkIntegrationInfoId(integrationId, Operation.READ);
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/{customerId}/integration/{integrationId}", method = RequestMethod.POST)
    @ResponseBody
    public Integration assignIntegrationToCustomer(@ApiParam(value = CUSTOMER_ID_PARAM_DESCRIPTION) @PathVariable("customerId") String strCustomerId,
                                       @ApiParam(value = INTEGRATION_ID_PARAM_DESCRIPTION) @PathVariable(INTEGRATION_ID) String strIntegrationId) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        checkParameter(INTEGRATION_ID, strIntegrationId);
        CustomerId customerId = new CustomerId(toUUID(strCustomerId));
        Customer customer = checkCustomerId(customerId, Operation.READ);
        IntegrationId integrationId = new IntegrationId(toUUID(strIntegrationId));
        checkIntegrationId(integrationId, Operation.ASSIGN_TO_CUSTOMER);
        return tbIntegrationService.assignIntegrationToCustomer(getTenantId(), integrationId, customer, getCurrentUser());
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/integration/{integrationId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Integration unassignIntegrationFromCustomer(@ApiParam(value = INTEGRATION_ID_PARAM_DESCRIPTION) @PathVariable(INTEGRATION_ID) String strIntegrationId) throws ThingsboardException {
        checkParameter(INTEGRATION_ID, strIntegrationId);
        IntegrationId integrationId = new IntegrationId(toUUID(strIntegrationId));
        Integration  integration = checkIntegrationId(integrationId, Operation.UNASSIGN_FROM_CUSTOMER);
        if (integration.getCustomerId() == null || integration.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
            throw new IncorrectParameterException("Integration isn't assigned to any customer!");
        }
        Customer customer = checkCustomerId(integration.getCustomerId(), Operation.READ);
        return tbIntegrationService.unassignIntegrationToCustomer(getTenantId(), integrationId, customer, getCurrentUser());
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/integrations", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Integration> getTenantIntegrations(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION)
            @RequestParam int page,
            @ApiParam(value = INTEGRATION_TYPE_DESCRIPTION)
            @RequestParam(required = false) String type,
            @ApiParam(value = INTEGRATION_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = INTEGRATION_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        if (type != null && type.trim().length() > 0) {
            return checkNotNull(integrationService.findIntegrationsByTenantIdAndType(tenantId, type, pageLink));
        } else {
            return checkNotNull(integrationService.findIntegrationsByTenantId(tenantId, pageLink));
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/integrations", params = {"pageSize", "page", "sortProperty", "sortOrder", "type"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Integration> getIntegrations(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION)
            @RequestParam int page,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = INTEGRATION_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) SortOrder sortOrder,
            @ApiParam(value = TYPE)
            @RequestParam String type) throws ThingsboardException {
        return checkNotNull(integrationService.findIntegrations(pageSize, page, sortProperty, sortOrder, type));
    }


    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/integrationInfos", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<IntegrationInfo> getTenantIntegrationViewInfos(
           @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = ENTITY_VIEW_TYPE)
            @RequestParam(required = false) String type,
            @ApiParam(value = INTEGRATION_PROFILE_ID_PARAM_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues =  INTEGRATION_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        if (type != null && type.trim().length() > 0) {
            return checkNotNull(integrationService.findIntegrationInfosByTenantIdAndType(tenantId, type, pageLink));
        } else {
            return checkNotNull(integrationService.findIntegrationInfosByTenantId(tenantId, pageLink));
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/integrations", params = {"integrationName"}, method = RequestMethod.GET)
    @ResponseBody
    public Integration getTenantIntegration(
            @ApiParam(value = INTEGRATION_NAME_DESCRIPTION)
            @RequestParam String integrationName) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        return checkNotNull(integrationService.findIntegrationByTenantIdAndName(tenantId, integrationName));
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/customer/{customerId}/integrations", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Integration> getCustomerIntegrations(
            @ApiParam(value = CUSTOMER_ID_PARAM_DESCRIPTION)
            @PathVariable("customerId") String strCustomerId,
            @ApiParam(value = PAGE_SIZE_DESCRIPTION)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION)
            @RequestParam int page,
            @ApiParam(value = INTEGRATION_TYPE_DESCRIPTION)
            @RequestParam(required = false) String type,
            @ApiParam(value = INTEGRATION_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = INTEGRATION_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        TenantId tenantId = getCurrentUser().getTenantId();
        CustomerId customerId = new CustomerId(toUUID(strCustomerId));
        checkCustomerId(customerId, Operation.READ);
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        if (type != null && type.trim().length() > 0) {
            return checkNotNull(integrationService.findIntegrationsByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink));
        } else {
            return checkNotNull(integrationService.findIntegrationsByTenantIdAndCustomerId(tenantId, customerId, pageLink));
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/integrations", params = {"integrationIds"}, method = RequestMethod.GET)
    @ResponseBody
    public List<Integration> getIntegrationsByIds(
            @ApiParam(value = "A list of integrations ids, separated by comma ','")
            @RequestParam("integrationIds") String[] strIntegrationIds) throws ThingsboardException, ExecutionException, InterruptedException {
        checkArrayParameter("integrationIds", strIntegrationIds);
        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        CustomerId customerId = user.getCustomerId();
        List<IntegrationId> integrationIds = new ArrayList<>();
        for (String strIntegrationId : strIntegrationIds) {
            integrationIds.add(new IntegrationId(toUUID(strIntegrationId)));
        }
        ListenableFuture<List<Integration>> integrations;
        if (customerId == null || customerId.isNullUid()) {
            integrations = integrationService.findIntegrationsByTenantIdAndIdsAsync(tenantId, integrationIds);
        } else {
            integrations = integrationService.findIntegrationsByTenantIdCustomerIdAndIdsAsync(tenantId, customerId, integrationIds);
        }
        return checkNotNull(integrations.get());
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/integrations", method = RequestMethod.POST)
    @ResponseBody
    public List<Integration> findByQuery(@RequestBody IntegrationSearchQuery query) throws ThingsboardException, ExecutionException, InterruptedException {
        checkNotNull(query);
        checkNotNull(query.getParameters());
        checkNotNull(query.getIntegrationTypes());
        checkEntityId(query.getParameters().getEntityId(), Operation.READ);
        List<Integration> integrations = checkNotNull(integrationService.findIntegrationsByQuery(getTenantId(), query).get());
        integrations = integrations.stream().filter(integration -> {
            try {
                accessControlService.checkPermission(getCurrentUser(), Resource.INTEGRATION, Operation.READ, integration.getId(), integration);
                return true;
            } catch (ThingsboardException e) {
                return false;
            }
        }).collect(Collectors.toList());
        return integrations;
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/public/integration/{integrationId}", method = RequestMethod.POST)
    @ResponseBody
    public Integration assignIntegrationToPublicCustomer(@ApiParam(value = INTEGRATION_ID_PARAM_DESCRIPTION) @PathVariable(INTEGRATION_ID) String strIntegrationId) throws ThingsboardException {
        checkParameter(INTEGRATION_ID, strIntegrationId);
        IntegrationId integrationId = new IntegrationId(toUUID(strIntegrationId));
        checkIntegrationId(integrationId, Operation.ASSIGN_TO_CUSTOMER);
        return tbIntegrationService.assignIntegrationToPublicCustomer(getTenantId(), integrationId, getCurrentUser());
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/edge/{edgeId}/integration/{integrationId}", method = RequestMethod.POST)
    @ResponseBody
    public Integration assignIntegrationToEdge(@ApiParam(value = EDGE_ID_PARAM_DESCRIPTION) @PathVariable(EDGE_ID) String strEdgeId,
                                   @ApiParam(value = INTEGRATION_ID_PARAM_DESCRIPTION) @PathVariable(INTEGRATION_ID) String strIntegrationId) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        checkParameter(INTEGRATION_ID, strIntegrationId);

        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        Edge edge = checkEdgeId(edgeId, Operation.READ);

        IntegrationId integrationId = new IntegrationId(toUUID(strIntegrationId));
        checkIntegrationId(integrationId, Operation.READ);

        return tbIntegrationService.assignIntegrationToEdge(getTenantId(), integrationId, edge, getCurrentUser());
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/edge/{edgeId}/integration/{integrationId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Integration unassignIntegrationFromEdge(@ApiParam(value = EDGE_ID_PARAM_DESCRIPTION) @PathVariable(EDGE_ID) String strEdgeId,
                                       @ApiParam(value = INTEGRATION_ID_PARAM_DESCRIPTION) @PathVariable(INTEGRATION_ID) String strIntegrationId) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        checkParameter(INTEGRATION_ID, strIntegrationId);
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        Edge edge = checkEdgeId(edgeId, Operation.READ);

        IntegrationId integrationId = new IntegrationId(toUUID(strIntegrationId));
        Integration integration = checkIntegrationId(integrationId, Operation.READ);

        return tbIntegrationService.unassignIntegrationFromEdge(getTenantId(), integration, edge, getCurrentUser());
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/edge/{edgeId}/integrations", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Integration> getEdgeIntegrations(
            @ApiParam(value = EDGE_ID_PARAM_DESCRIPTION)
            @PathVariable(EDGE_ID) String strEdgeId,
            @ApiParam(value = PAGE_SIZE_DESCRIPTION)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION)
            @RequestParam int page,
            @ApiParam(value = INTEGRATION_TYPE_DESCRIPTION)
            @RequestParam(required = false) String type,
            @ApiParam(value = INTEGRATION_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = INTEGRATION_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder,
            @ApiParam(value = "Timestamp. Integrations with creation time before it won't be queried")
            @RequestParam(required = false) Long startTime,
            @ApiParam(value = "Timestamp. Integrations with creation time after it won't be queried")
            @RequestParam(required = false) Long endTime) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        TenantId tenantId = getCurrentUser().getTenantId();
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        checkEdgeId(edgeId, Operation.READ);
        TimePageLink pageLink = createTimePageLink(pageSize, page, textSearch, sortProperty, sortOrder, startTime, endTime);
        PageData<Integration> nonFilteredResult;
        if (type != null && type.trim().length() > 0) {
            nonFilteredResult = integrationService.findIntegrationsByTenantIdAndEdgeIdAndType(tenantId, edgeId, type, pageLink);
        } else {
            nonFilteredResult = integrationService.findIntegrationsByTenantIdAndEdgeId(tenantId, edgeId, pageLink);
        }
        List<Integration> filteredIntegrations = nonFilteredResult.getData().stream().filter(integration -> {
            try {
                accessControlService.checkPermission(getCurrentUser(), Resource.INTEGRATION, Operation.READ, integration.getId(), integration);
                return true;
            } catch (ThingsboardException e) {
                return false;
            }
        }).collect(Collectors.toList());
        PageData<Integration> filteredResult = new PageData<>(filteredIntegrations,
                nonFilteredResult.getTotalPages(),
                nonFilteredResult.getTotalElements(),
                nonFilteredResult.hasNext());
        return checkNotNull(filteredResult);
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @PostMapping("/integration/bulk_import")
    public BulkImportResult<Integration> processIntegrationsBulkImport(@RequestBody BulkImportRequest request) throws Exception {
        SecurityUser user = getCurrentUser();
        return integrationBulkImportService.processBulkImport(request, user);
    }
}
