package org.thingsboard.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.script.api.decoder.TestDecoderService;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.data_converter.DataConverter;
import org.thingsboard.server.common.data.data_converter.DataConverterInfo;
import org.thingsboard.server.common.data.data_converter.DataConverterSearchQuery;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.script.ScriptLanguage;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportRequest;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportResult;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.data_converter.DataConverterBulkImportService;
import org.thingsboard.server.service.entity.data_converter.TbDataConverterService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.thingsboard.server.controller.ControllerConstants.*;
import static org.thingsboard.server.controller.EdgeController.EDGE_ID;


@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j

public class DataConverterController extends BaseController {

    private final DataConverterBulkImportService dataConverterBulkImportService;
    private final TbDataConverterService tbDataConverterService;
    private final TestDecoderService testDecoderService;


    public static final String DATACONVERTER_ID = "dataConverterId";

    @ApiOperation(value = "Get DataConverter (getDataConverterById)",
            notes = "Fetch the DataConverter object based on the provided DataConverter Id. " +
                    "If the user has the authority of 'Tenant Administrator', the server checks that the dataConverter is owned by the same tenant. " +
                    "If the user has the authority of 'Customer User', the server checks that the dataConverter is assigned to the same customer." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH
            , produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/converters/{dataConverterId}", method = RequestMethod.GET)
    @ResponseBody
    public DataConverter getDataConverterById(@ApiParam(value = "Data Converter ID") @PathVariable(DATACONVERTER_ID) String strDataConverterId) throws ThingsboardException {
        checkParameter(DATACONVERTER_ID, strDataConverterId);
        DataConverterId dataConverterId = new DataConverterId(toUUID(strDataConverterId));
        return checkDataConverterId(dataConverterId, Operation.READ);

    }

    @ApiOperation(value = "Create Or Update DataConverter (saveDataConverter)",
            notes = "Creates or Updates the DataConverter. When creating dataConverter, platform generates DataConverter Id as " + UUID_WIKI_LINK +
                    "The newly created DataConverter id will be present in the response. " +
                    "Specify existing DataConverter id to update the dataConverter. " +
                    "Referencing non-existing DataConverter Id will cause 'Not Found' error. " +
                    "Remove 'id', 'tenantId' and optionally 'customerId' from the request body example (below) to create new DataConverter entity. "
                    + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/converters", method = RequestMethod.POST)
    @ResponseBody
    public DataConverter saveDataConverter(@ApiParam(value = "A JSON value representing the Data.") @RequestBody DataConverter dataConverter) throws Exception {
        dataConverter.setTenantId(getTenantId());
        checkEntity(dataConverter.getId(), dataConverter, Resource.DATACONVERTER);
        return tbDataConverterService.save(dataConverter, getCurrentUser());
    }

    @ApiOperation(value = "Delete dataConverter (deleteDataConverter)", notes = "Deletes the dataConverter and all the relations (from and to the dataConverter). Referencing non-existing dataConverter Id will cause an error." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/converters/{dataConverterId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteDataConverter(@ApiParam(value = DATACONVERTER_ID_PARAM_DESCRIPTION) @PathVariable(DATACONVERTER_ID) String strDataConverterId) throws Exception {
        checkParameter(DATACONVERTER_ID, strDataConverterId);
        DataConverterId dataConverterId = new DataConverterId(toUUID(strDataConverterId));
        DataConverter dataConverter = checkDataConverterId(dataConverterId, Operation.DELETE);
        tbDataConverterService.delete(dataConverter, getCurrentUser());
    }

    @ApiOperation(value = "Make dataConverter publicly available (assignDataConverterToPublicCustomer)",
            notes = "DataConverter will be available for non-authorized (not logged-in) users. " +
                    "This is useful to create dashboards that you plan to share/embed on a publicly available website. " +
                    "However, users that are logged-in and belong to different tenant will not be able to access the dataConverter." + TENANT_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/public/converters/{convertersId}", method = RequestMethod.POST)
    @ResponseBody
    public DataConverter assignDataConverterToPublicCustomer(@ApiParam(value = DATACONVERTER_ID_PARAM_DESCRIPTION) @PathVariable(DATACONVERTER_ID) String strDataConverterId) throws ThingsboardException {
        checkParameter(DATACONVERTER_ID, strDataConverterId);
        DataConverterId dataConverterId = new DataConverterId(toUUID(strDataConverterId));
        checkDataConverterId(dataConverterId, Operation.ASSIGN_TO_CUSTOMER);
        return tbDataConverterService.assignDataConverterToPublicCustomer(getTenantId(), dataConverterId, getCurrentUser());
    }

    @ApiOperation(value = "Get Tenant DataConverters (getTenantDataConverters)", notes = "Returns a page of dataConverters owned by tenant. " + PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/converters", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<DataConverter> getTenantDataConverter(@ApiParam(value = PAGE_SIZE_DESCRIPTION) @RequestParam int pageSize, @ApiParam(value = PAGE_NUMBER_DESCRIPTION) @RequestParam int page, @ApiParam(value = DATACONVERTER_TYPE_DESCRIPTION) @RequestParam(required = false) String type, @ApiParam(value = DATACONVERTER_TEXT_SEARCH_DESCRIPTION) @RequestParam(required = false) String textSearch, @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = DATACONVERTER_SORT_PROPERTY_ALLOWABLE_VALUES) @RequestParam(required = false) String sortProperty, @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES) @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        if (type != null && type.trim().length() > 0) {
            return checkNotNull(dataConverterService.findDataConverterByTenantIdAndType(tenantId, type, pageLink));
        } else {
            return checkNotNull(dataConverterService.findDataConverterByTenantId(tenantId, pageLink));
        }
    }

    @ApiOperation(value = "Get Tenant DataConverter Infos (getTenantDataConverterInfos)", notes = "Returns a page of dataConverters info objects owned by tenant. " + PAGE_DATA_PARAMETERS + DATACONVERTER_INFO_DESCRIPTION + TENANT_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/convertersInfos", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<DataConverterInfo> getTenantDataConverterInfos(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION)
            @RequestParam int page,
            @ApiParam(value = DATACONVERTER_TYPE_DESCRIPTION)
            @RequestParam(required = false) String type,
            @ApiParam(value = DATACONVERTER_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = DATACONVERTER_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        if (type != null && type.trim().length() > 0) {
            return checkNotNull(dataConverterService.findDataConverterInfosByTenantIdAndType(tenantId, type, pageLink));
        } else {
            return checkNotNull(dataConverterService.findDataConverterInfosByTenantId(tenantId, pageLink));
        }
    }

    @ApiOperation(value = "Find related dataConverters (findByQuery)",
            notes = "Returns all dataConverters that are related to the specific entity. " +
                    "The entity id, relation type, dataConverter types, depth of the search, and other query parameters defined using complex 'DataConverterSearchQuery' object. " +
                    "See 'Model' tab of the Parameters for more info.", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/converter", method = RequestMethod.POST)
    @ResponseBody
    public List<DataConverter> findByQuery(@RequestBody DataConverterSearchQuery query) throws ThingsboardException, ExecutionException, InterruptedException {
        checkNotNull(query);
        checkNotNull(query.getParameters());
        checkNotNull(query.getDataConverterTypes());
        checkEntityId(query.getParameters().getEntityId(), Operation.READ);
        List<DataConverter> dataConverter = checkNotNull(dataConverterService.findDataConverterByQuery(getTenantId(), query).get());
        dataConverter = dataConverter.stream().filter(dataConverters -> {
            try {
                accessControlService.checkPermission(getCurrentUser(), Resource.DATACONVERTER, Operation.READ, dataConverters.getId(), dataConverters);
                return true;
            } catch (ThingsboardException e) {
                return false;
            }
        }).collect(Collectors.toList());
        return dataConverter;
    }

    @ApiOperation(value = "Get DataConverter Types (getDataConverterTypes)", notes = "Deprecated. See 'getDataConverterProfileNames' API from DataConverter Profile Controller instead." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/converters/types", method = RequestMethod.GET)
    @ResponseBody
    @Deprecated(since = "3.6.2")

    public List<EntitySubtype> getDataConverterTypes() throws ThingsboardException, ExecutionException, InterruptedException {

        SecurityUser user = getCurrentUser();
        TenantId tenantId = user.getTenantId();
        ListenableFuture<List<EntitySubtype>> dataConverterTypes = dataConverterService.findDataConverterTypesByTenantId(tenantId);
        return checkNotNull(dataConverterTypes.get());
    }

    @ApiOperation(value = "Assign dataConverter to edge (assignDataConverterToEdge)", notes = "Creates assignment of an existing dataConverter to an instance of The Edge. " + EDGE_ASSIGN_ASYNC_FIRST_STEP_DESCRIPTION + "Second, remote edge service will receive a copy of assignment dataConverter " + EDGE_ASSIGN_RECEIVE_STEP_DESCRIPTION + "Third, once dataConverter will be delivered to edge service, it's going to be available for usage on remote edge instance.", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/edge/{edgeId}/converters/{convertersId}", method = RequestMethod.POST)
    @ResponseBody
    public DataConverter assignDataConverterToEdge(@ApiParam(value = EDGE_ID_PARAM_DESCRIPTION) @PathVariable(EDGE_ID) String strEdgeId, @ApiParam(value = DATACONVERTER_ID_PARAM_DESCRIPTION) @PathVariable(DATACONVERTER_ID) String strDataConverterId) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        checkParameter(DATACONVERTER_ID, strDataConverterId);

        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        Edge edge = checkEdgeId(edgeId, Operation.READ);

        DataConverterId dataConverterId = new DataConverterId(toUUID(strDataConverterId));
        checkDataConverterId(dataConverterId, Operation.READ);

        return tbDataConverterService.assignDataConverterToEdge(getTenantId(), dataConverterId, edge, getCurrentUser());
    }

    @ApiOperation(value = "Unassign dataConverter from edge (unassignDataConverterFromEdge)", notes = "Clears assignment of the dataConverter to the edge. " + EDGE_UNASSIGN_ASYNC_FIRST_STEP_DESCRIPTION + "Second, remote edge service will receive an 'unassign' command to remove dataConverter " + EDGE_UNASSIGN_RECEIVE_STEP_DESCRIPTION + "Third, once 'unassign' command will be delivered to edge service, it's going to remove dataConverter locally.", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/edge/{edgeId}/converters/{convertersId}", method = RequestMethod.DELETE)
    @ResponseBody
    public DataConverter unassignDataConverterFromEdge(@ApiParam(value = EDGE_ID_PARAM_DESCRIPTION) @PathVariable(EDGE_ID) String strEdgeId, @ApiParam(value = DATACONVERTER_ID_PARAM_DESCRIPTION) @PathVariable(DATACONVERTER_ID) String strDataConverterId) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        checkParameter(DATACONVERTER_ID, strDataConverterId);
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        Edge edge = checkEdgeId(edgeId, Operation.READ);

        DataConverterId dataConverterId = new DataConverterId(toUUID(strDataConverterId));
        DataConverter dataConverter = checkDataConverterId(dataConverterId, Operation.READ);

        return tbDataConverterService.unassignDataConverterFromEdge(getTenantId(), dataConverter, edge, getCurrentUser());
    }

    @ApiOperation(value = "Get dataConverters assigned to edge (getEdgeDataConverters)",
            notes = "Returns a page of dataConverters assigned to edge. " +
                    PAGE_DATA_PARAMETERS, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/edge/{edgeId}/converters", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<DataConverter> getEdgeDataConverter(@ApiParam(value = EDGE_ID_PARAM_DESCRIPTION) @PathVariable(EDGE_ID) String strEdgeId, @ApiParam(value = PAGE_SIZE_DESCRIPTION) @RequestParam int pageSize, @ApiParam(value = PAGE_NUMBER_DESCRIPTION) @RequestParam int page, @ApiParam(value = DATACONVERTER_TYPE_DESCRIPTION) @RequestParam(required = false) String type, @ApiParam(value = DATACONVERTER_TEXT_SEARCH_DESCRIPTION) @RequestParam(required = false) String textSearch, @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = DATACONVERTER_SORT_PROPERTY_ALLOWABLE_VALUES) @RequestParam(required = false) String sortProperty, @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES) @RequestParam(required = false) String sortOrder, @ApiParam(value = "Timestamp. DataConverter with creation time before it won't be queried") @RequestParam(required = false) Long startTime, @ApiParam(value = "Timestamp. DataConverter with creation time after it won't be queried") @RequestParam(required = false) Long endTime) throws ThingsboardException {
        checkParameter(EDGE_ID, strEdgeId);
        TenantId tenantId = getCurrentUser().getTenantId();
        EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
        checkEdgeId(edgeId, Operation.READ);
        TimePageLink pageLink = createTimePageLink(pageSize, page, textSearch, sortProperty, sortOrder, startTime, endTime);
        PageData<DataConverter> nonFilteredResult;
        if (type != null && type.trim().length() > 0) {
            nonFilteredResult = dataConverterService.findDataConverterByTenantIdAndEdgeIdAndType(tenantId, edgeId, type, pageLink);
        } else {
            nonFilteredResult = dataConverterService.findDataConverterByTenantIdAndEdgeId(tenantId, edgeId, pageLink);
        }
        List<DataConverter> filteredDataConverter = nonFilteredResult.getData().stream().filter(dataConverter -> {
            try {
                accessControlService.checkPermission(getCurrentUser(), Resource.DATACONVERTER, Operation.READ, dataConverter.getId(), dataConverter);
                return true;
            } catch (ThingsboardException e) {
                return false;
            }
        }).collect(Collectors.toList());
        PageData<DataConverter> filteredResult = new PageData<>(filteredDataConverter, nonFilteredResult.getTotalPages(), nonFilteredResult.getTotalElements(), nonFilteredResult.hasNext());
        return checkNotNull(filteredResult);
    }

    @ApiOperation(value = "Import the bulk of dataConverters (processDataConvertersBulkImport)", notes = "There's an ability to import the bulk of dataConverters using the only .csv file.", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @PostMapping("/converters/bulk_import")
    public BulkImportResult<DataConverter> processDataConvertersBulkImport(@RequestBody BulkImportRequest request) throws Exception {
        SecurityUser user = getCurrentUser();
        return dataConverterBulkImportService.processBulkImport(request, user);
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/converter/{converterId}/debugIn", method = RequestMethod.GET)
    @ResponseBody
    public PageData<DataConverter> getConverterDebugInfo(@ApiParam(value = "Converter ID") @PathVariable("converterId") String converterId, @ApiParam(value = "Converter Type") @RequestParam(required = false, defaultValue = "") String converterType) throws ThingsboardException {
        try {
            if (converterType == null || converterType.trim().isEmpty()) {
                converterType = "";
            }
            return checkNotNull(dataConverterService.getConverterDebugInfo(converterId, converterType));
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    @RequestMapping(value = "/converters/{converterId}/debugIn", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Void> getConverterDebugIn(
            @ApiParam(value = "Converter ID") @PathVariable("converterId") UUID converterId,
            @ApiParam(value = "Converter Type") @RequestParam("converterType") String converterType) throws ThingsboardException {
        try {
            dataConverterService.getConverterDebugIn(converterId, converterType);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Test UpLink Decoder function",
            notes = TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/converter/testUpLink", method = RequestMethod.POST)
    @ResponseBody
    public JsonNode testDecoderUpLink(
            @ApiParam(value = "Script language: JS or TBEL")
            @RequestParam ScriptLanguage scriptLang,
            @ApiParam(value = "Test JS request. See API call description above.")
            @RequestBody JsonNode inputParams) throws ThingsboardException, IOException {
        String script = inputParams.get("script").asText();
        String base64Payload = inputParams.get("payload").asText();
        byte[] decodedPayload = Base64.getDecoder().decode(base64Payload);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode payloadJson = objectMapper.readTree(decodedPayload);
        JsonNode metadataJson = inputParams.get("metadata");
        Map<String, String> metadata = JacksonUtil.convertValue(metadataJson, new TypeReference<Map<String, String>>() {
        });
        try {
            JsonNode decodedData = testDecoderService.executeScript(script, payloadJson, metadata);
        } catch (Exception e) {

        }
        return null;
    }

    @ApiOperation(value = "Test DownLink Encoder function",
            notes = TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/converter/testDownLink", method = RequestMethod.POST)
    @ResponseBody
    public JsonNode testEncoderDownLink(
            @ApiParam(value = "Script language: JS or TBEL")
            @RequestParam ScriptLanguage scriptLang,
            @ApiParam(value = "Test JS request. See API call description above.")
            @RequestBody JsonNode inputParams) throws ThingsboardException, IOException {
        String script = inputParams.get("script").asText();
        String base64Payload = inputParams.get("payload").asText();
        byte[] decodedPayload = Base64.getDecoder().decode(base64Payload);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode payloadJson = objectMapper.readTree(decodedPayload);
        JsonNode metadataJson = inputParams.get("metadata");
        Map<String, String> metadata = JacksonUtil.convertValue(metadataJson, new TypeReference<Map<String, String>>() {
        });
        try {
            JsonNode decodedData = testDecoderService.executeScript(script, payloadJson, metadata);
        } catch (Exception e) {

        }
        return null;
    }

    @ApiOperation(value = "Get Data Convert infos (getDataConvertInfos)",
            notes = "Returns a page of data Convert  info objects owned by tenant. " +
                    PAGE_DATA_PARAMETERS + DATA_CONVERT_INFO_DESCRIPTION + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH,
            produces = "application/json")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/dataConvertInfos", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<DataConverterInfo> getDataConvertInfos(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = DATA_CONVERT_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = DATA_CONVERT_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return checkNotNull(dataConverterService.findDataConvertInfos(getTenantId(), pageLink));
    }

}



