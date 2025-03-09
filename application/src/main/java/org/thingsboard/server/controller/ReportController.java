package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.Report;
import org.thingsboard.server.common.data.ReportInfo;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.ReportId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.service.entity.report.TbReportService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.thingsboard.server.controller.ControllerConstants.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ReportController extends BaseController {

    private final TbReportService tbReportService;

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/report/info/{reportId}", method = RequestMethod.GET)
    @ResponseBody
    public ReportInfo getReportInfoById(@ApiParam(value = "") @PathVariable(REPORT_ID) String strReportId) throws ThingsboardException {
        checkParameter(REPORT_ID, strReportId);
        ReportId reportId = new ReportId(toUUID(strReportId));
        return checkReportInfoId(reportId, Operation.READ);
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    public Report saveReport(@ApiParam(value = "A JSON value representing the report.") @RequestBody Report report) throws Exception {
        report.setTenantId(getTenantId());
        checkEntity(report.getId(), report, Resource.REPORT);
        return tbReportService.save(report, getCurrentUser());
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/{customerId}/report/{reportId}", method = RequestMethod.POST)
    @ResponseBody
    public String assignReportToCustomer(@ApiParam(value = "") @PathVariable("customerId") String customerId, @ApiParam(value = "") @PathVariable(REPORT_ID) String reportId) throws ThingsboardException {
        return reportId + " :assigned to " + customerId;
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/report/{reportId}", method = RequestMethod.DELETE)
    @ResponseBody
    public String unassignReportFromCustomer(@ApiParam(value = "") @PathVariable(REPORT_ID) String reportId) throws ThingsboardException {
        return reportId + " :unassigned";
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/report/{reportId}/customers", method = RequestMethod.POST)
    @ResponseBody
    public Report updateReportCustomers(@ApiParam(value = "") @PathVariable(REPORT_ID) String strReportId, @ApiParam(value = "") @RequestBody(required = false) String[] strCustomerIds) throws ThingsboardException {
        checkParameter(REPORT_ID, strReportId);
        ReportId reportId = new ReportId(toUUID(strReportId));
        Report report = checkReportId(reportId, Operation.ASSIGN_TO_CUSTOMER);
        Set<CustomerId> customerIds = customerIdFromStr(strCustomerIds);
        return tbReportService.updateReportCustomers(report, customerIds, getCurrentUser());
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/report/{reportId}/customers/add", method = RequestMethod.POST)
    public Report addReportCustomers(@ApiParam(value = "") @PathVariable(REPORT_ID) String strReportId, @ApiParam(value = "") @RequestBody String[] strCustomerIds) throws ThingsboardException {
        checkParameter(REPORT_ID, strReportId);
        ReportId reportId = new ReportId(toUUID(strReportId));
        Report report = checkReportId(reportId, Operation.ASSIGN_TO_CUSTOMER);
        Set<CustomerId> customerIds = customerIdFromStr(strCustomerIds);
        return tbReportService.addReportCustomers(report, customerIds, getCurrentUser());
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/report/{reportId}/customers/remove", method = RequestMethod.POST)
    @ResponseBody
    public Report removeReportCustomers(@ApiParam(value = "") @PathVariable(REPORT_ID) String strReportId, @ApiParam(value = "") @RequestBody String[] strCustomerIds) throws ThingsboardException {
        checkParameter(REPORT_ID, strReportId);
        ReportId reportId = new ReportId(toUUID(strReportId));
        Report report = checkReportId(reportId, Operation.UNASSIGN_FROM_CUSTOMER);
        Set<CustomerId> customerIds = customerIdFromStr(strCustomerIds);
        return tbReportService.removeReportCustomers(report, customerIds, getCurrentUser());
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/reports", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<ReportInfo> getTenantReports(@ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true) @RequestParam int pageSize, @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true) @RequestParam int page, @ApiParam(value = REPORT_TEXT_SEARCH_DESCRIPTION) @RequestParam(required = false) String textSearch, @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = REPORT_SORT_PROPERTY_ALLOWABLE_VALUES) @RequestParam(required = false) String sortProperty, @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES) @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return checkNotNull(reportService.findReportsByTenantId(tenantId, pageLink));
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/customer/{customerId}/reports", method = RequestMethod.GET)
    @ResponseBody
    public String getCustomerReports(@ApiParam(value = "", required = true) @PathVariable("customerId") String customerId) throws ThingsboardException {
        return customerId + ": ReportsAllCustomerData";
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/report/{reportId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteReport(@ApiParam(value = "") @PathVariable(REPORT_ID) String strReportId) throws Exception {
        checkParameter(REPORT_ID, strReportId);
        ReportId reportId = new ReportId(toUUID(strReportId));
        Report report = checkReportId(reportId, Operation.DELETE);
        tbReportService.delete(report, getCurrentUser());
    }

    private Set<CustomerId> customerIdFromStr(String[] strCustomerIds) {
        Set<CustomerId> customerIds = new HashSet<>();
        if (strCustomerIds != null) {
            for (String strCustomerId : strCustomerIds) {
                customerIds.add(new CustomerId(UUID.fromString(strCustomerId)));
            }
        }
        return customerIds;
    }
}
