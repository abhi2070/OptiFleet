package org.thingsboard.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.SchedulerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.scheduler.EmailConfiguration;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.common.data.scheduler.SchedulerSearchQuery;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportRequest;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportResult;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.scheduler.TbSchedulerService;
import org.thingsboard.server.service.scheduler.SchedulerBulkImportService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import javax.mail.MessagingException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.thingsboard.server.controller.ControllerConstants.*;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class SchedulerController extends BaseController {
    private final SchedulerBulkImportService schedulerBulkImportService;
    private final TbSchedulerService tbSchedulerService;

    private final MailService mailService;

    public static final String SCHEDULER_ID = "schedulerId";


    @ApiOperation(value = "Get Scheduler (getSchedulertById)", notes = "Fetch the Scheduler object based on the provided Scheduler Id. " + "If the user has the authority of 'Tenant Administrator', the server checks that the scheduler is owned by the same tenant. ", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/schedulers/{schedulerId}", method = RequestMethod.GET)
    @ResponseBody
    public Scheduler getSchedulerById(@ApiParam(value = SCHEDULER_ID_PARAM_DESCRIPTION) @PathVariable(SCHEDULER_ID) String strSchedulerId) throws ThingsboardException {
        checkParameter(SCHEDULER_ID, strSchedulerId);
        SchedulerId schedulerId = new SchedulerId(toUUID(strSchedulerId));
        return checkSchedulerId(schedulerId, Operation.READ);
    }

    @ApiOperation(value = "Create Or Update Scheduler (saveScheduler)", notes = "Creates or Updates the Scheduler. When creating scheduler, platform generates Scheduler Id as " + UUID_WIKI_LINK + "The newly created Scheduler id will be present in the response. " + "Specify existing Scheduler id to update the scheduler. " + "Referencing non-existing Scheduler Id will cause 'Not Found' error. " + "Remove 'id', 'tenantId' and optionally 'customerId' from the request body example (below) to create new Scheduler entity. " + TENANT_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/scheduler", method = RequestMethod.POST)
    @ResponseBody
    public Scheduler saveScheduler(@ApiParam(value = "A JSON value representing the scheduler.") @RequestBody Scheduler scheduler) throws Exception {
        scheduler.setTenantId(getTenantId());
        if(scheduler.getEndDate() != null){
            scheduler.setActive(true);
        }else {
            scheduler.setActive(false);
        }
        checkEntity(scheduler.getId(), scheduler, Resource.SCHEDULER);
        Scheduler savedScheduler = tbSchedulerService.save(scheduler, getCurrentUser());
        return savedScheduler;
    }


    @ApiOperation(value = "Delete scheduler (deleteScheduler)", notes = "Deletes the scheduler and all the relations (from and to the scheduler). Referencing non-existing scheduler Id will cause an error." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/schedulers/{schedulerId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteScheduler(@ApiParam(value = SCHEDULER_ID_PARAM_DESCRIPTION) @PathVariable(SCHEDULER_ID) String strSchedulerId) throws Exception {
        checkParameter(SCHEDULER_ID, strSchedulerId);
        SchedulerId schedulerId = new SchedulerId(toUUID(strSchedulerId));
        Scheduler scheduler = checkSchedulerId(schedulerId, Operation.DELETE);
        tbSchedulerService.delete(scheduler, getCurrentUser());
    }

    @ApiOperation(value = "Get Tenant Scheduler (getTenantScheduler)", notes = "Returns a page of scheduler owned by tenant. " + PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/scheduler", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Scheduler> getTenantSchedulerInfos(@ApiParam(value = PAGE_SIZE_DESCRIPTION) @RequestParam int pageSize, @ApiParam(value = PAGE_NUMBER_DESCRIPTION) @RequestParam int page, @ApiParam(value = SCHEDULER_TYPE_DESCRIPTION) @RequestParam(required = false) String type, @ApiParam(value = SCHEDULER_TEXT_SEARCH_DESCRIPTION) @RequestParam(required = false) String textSearch, @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = SCHEDULER_SORT_PROPERTY_ALLOWABLE_VALUES) @RequestParam(required = false) String sortProperty, @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES) @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return checkNotNull(schedulerService.findSchedulersByTenantId(tenantId, pageLink));
    }


    @ApiOperation(value = "Find related scheduler (findByQuery)", notes = "Returns all scheduler that are related to the specific entity. " + "The entity id, relation type, scheduler types, depth of the search, and other query parameters defined using complex 'SchedulerSearchQuery' object. " + "See 'Model' tab of the Parameters for more info.", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/schedulers", method = RequestMethod.POST)
    @ResponseBody
    public List<Scheduler> findByQuery(@RequestBody SchedulerSearchQuery query) throws ThingsboardException, ExecutionException, InterruptedException {
        checkNotNull(query);
        checkNotNull(query.getParameters());
        checkEntityId(query.getParameters().getEntityId(), Operation.READ);

        List<Scheduler> schedulers = schedulerService.findSchedulerByQuery(getTenantId(), query).get();
        schedulers = schedulers.stream().filter(scheduler -> {
            try {
                accessControlService.checkPermission(getCurrentUser(), Resource.SCHEDULER, Operation.READ, scheduler.getId(), scheduler);
                return true;
            } catch (ThingsboardException e) {
                return false;
            }
        }).collect(Collectors.toList());

        return schedulers;
    }

    @ApiOperation(value = "Get Scheduler Info (getSchedulerInfoById)",
            notes = "Fetch the Scheduler Info object based on the provided Scheduler Id. " +
                    "If the user has the authority of 'Tenant Administrator', the server checks that the scheduler is owned by the same tenant. " +
                    SCHEDULER_INFO_DESCRIPTION + TENANT_AUTHORITY_PARAGRAPH, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/schedulers/info/{schedulerId}", method = RequestMethod.GET)
    @ResponseBody
    public Scheduler getSchedulerInfoById(@ApiParam(value = SCHEDULER_ID_PARAM_DESCRIPTION)
                                              @PathVariable(SCHEDULER_ID) String strSchedulerId) throws ThingsboardException {
        checkParameter(SCHEDULER_ID, strSchedulerId);
        SchedulerId schedulerId = new SchedulerId(toUUID(strSchedulerId));
        return checkSchedulerId(schedulerId, Operation.READ);
    }

    @ApiOperation(value = "Import the bulk of scheduler (processSchedulerBulkImport)", notes = "There's an ability to import the bulk of scheduler using the only .csv file.", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @PostMapping("/schedulers/bulk_import")
    public BulkImportResult<Scheduler> processSchedulerBulkImport(@RequestBody BulkImportRequest request) throws Exception {
        SecurityUser user = getCurrentUser();
        return schedulerBulkImportService.processBulkImport(request, user);
    }



    @ApiOperation(value = "Send or Schedule Email (sendOrScheduleEmail)",
            notes = "Send an email immediately or schedule it to be sent later.",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @PostMapping(value = "/sendEmail/scheduler", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.OK)
    public EmailConfiguration sendEmail(
            @RequestParam("emailConfig") String emailConfigJson,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments,
            @ApiParam(value = "Send email at specific time (in milliseconds since epoch)")
            @RequestParam(required = false) Long start,
            @ApiParam(value = "Repeat Schedule: NONE, DAILY, WEEKLY, MONTHLY, YEARLY")
            @RequestParam(defaultValue = "NONE") String repeatSchedule,
            @ApiParam(value = "End time for the email schedule (in milliseconds since epoch)")
            @RequestParam(required = false) Long endTime) throws ThingsboardException, MessagingException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        EmailConfiguration emailConfiguration = objectMapper.readValue(emailConfigJson, EmailConfiguration.class);

        mailService.emailScheduler(emailConfiguration, attachments);

        return emailConfiguration;
    }


    @ApiOperation(value = "Toggle Scheduler Active Status (toggleSchedulerStatus)",
            notes = "Enables or disables the automatic email sending for the specified scheduler.",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/schedulers/{schedulerId}/status", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public Scheduler toggleSchedulerStatus(@ApiParam(value = "Scheduler ID") @PathVariable(SCHEDULER_ID) String strSchedulerId) throws Exception {
        checkParameter(SCHEDULER_ID, strSchedulerId);
        SchedulerId schedulerId = new SchedulerId(toUUID(strSchedulerId));
        Scheduler scheduler = checkSchedulerId(schedulerId, Operation.READ);
        boolean currentActiveStatus = scheduler.getActive();
        scheduler.setActive(!currentActiveStatus);
        Scheduler updatedScheduler = tbSchedulerService.save(scheduler, getCurrentUser());
        return updatedScheduler;
    }


}






