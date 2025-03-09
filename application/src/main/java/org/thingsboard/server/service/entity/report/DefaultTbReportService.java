package org.thingsboard.server.service.entity.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.ReportId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.report.ReportService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.AbstractTbEntityService;

import java.util.HashSet;
import java.util.Set;

@Service
@TbCoreComponent
@AllArgsConstructor
@Slf4j
public class DefaultTbReportService extends AbstractTbEntityService implements TbReportService {

    private final ReportService reportService;

    @Override
    public Report save(Report report, User user) throws Exception {
        ActionType actionType = report.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = report.getTenantId();
        try {
            Report savedReport = checkNotNull(reportService.saveReport(report));
            autoCommit(user, savedReport.getId());
            notificationEntityService.logEntityAction(tenantId, savedReport.getId(), savedReport, null, actionType, user);
            return savedReport;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.REPORT), report, actionType, user, e);
            throw e;
        }
    }

    @Override
    public void delete(Report report, User user) {
        ActionType actionType = ActionType.DELETED;
        ReportId reportId = report.getId();
        TenantId tenantId = report.getTenantId();
        try {
            reportService.deleteReport(tenantId, reportId);
            notificationEntityService.logEntityAction(tenantId, reportId, report, null, actionType, user, reportId.toString());
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.REPORT), actionType, user, e, reportId.toString());
            throw e;
        }
    }

    @Override
    public Report assignReportToCustomer(Report report, Customer customer, User user) throws ThingsboardException {
        return null;
    }

    @Override
    public Report updateReportCustomers(Report report, Set<CustomerId> customerIds, User user) throws ThingsboardException {
        ActionType actionType = ActionType.ASSIGNED_TO_CUSTOMER;
        TenantId tenantId = report.getTenantId();
        ReportId reportId = report.getId();
        try {
            Set<CustomerId> addedCustomerIds = new HashSet<>();
            Set<CustomerId> removedCustomerIds = new HashSet<>();
            for (CustomerId customerId : customerIds) {
                if (!report.isAssignedToCustomer(customerId)) {
                    addedCustomerIds.add(customerId);
                }
            }
            Set<ShortCustomerInfo> assignedCustomers = report.getAssignedCustomers();
            if (assignedCustomers != null) {
                for (ShortCustomerInfo customerInfo : assignedCustomers) {
                    if (!customerIds.contains(customerInfo.getCustomerId())) {
                        removedCustomerIds.add(customerInfo.getCustomerId());
                    }
                }
            }

            if (addedCustomerIds.isEmpty() && removedCustomerIds.isEmpty()) {
                return report;
            } else {
                Report savedReport = null;
                for (CustomerId customerId : addedCustomerIds) {
                    savedReport = checkNotNull(reportService.assignReportToCustomer(tenantId, reportId, customerId));
                    ShortCustomerInfo customerInfo = savedReport.getAssignedCustomerInfo(customerId);
                    notificationEntityService.logEntityAction(tenantId, savedReport.getId(), savedReport, customerId, actionType, user, reportId.toString(), customerId.toString(), customerInfo.getTitle());
                }
                actionType = ActionType.UNASSIGNED_FROM_CUSTOMER;
                for (CustomerId customerId : removedCustomerIds) {
                    ShortCustomerInfo customerInfo = report.getAssignedCustomerInfo(customerId);
                    savedReport = checkNotNull(reportService.unassignReportFromCustomer(tenantId, reportId, customerId));
                    notificationEntityService.logEntityAction(tenantId, savedReport.getId(), savedReport, customerId, actionType, user, reportId.toString(), customerId.toString(), customerInfo.getTitle());
                }
                return savedReport;
            }
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.REPORT), actionType, user, e, reportId.toString());
            throw e;
        }
    }

    @Override
    public Report addReportCustomers(Report report, Set<CustomerId> customerIds, User user) throws ThingsboardException {
        ActionType actionType = ActionType.ASSIGNED_TO_CUSTOMER;
        TenantId tenantId = report.getTenantId();
        ReportId reportId = report.getId();
        try {
            Set<CustomerId> addedCustomerIds = new HashSet<>();
            for (CustomerId customerId : customerIds) {
                if (!report.isAssignedToCustomer(customerId)) {
                    addedCustomerIds.add(customerId);
                }
            }
            if (addedCustomerIds.isEmpty()) return report;
            else {
                Report savedReport = null;
                for (CustomerId customerId : addedCustomerIds) {
                    savedReport = checkNotNull(reportService.assignReportToCustomer(tenantId, reportId, customerId));
                    ShortCustomerInfo customerInfo = savedReport.getAssignedCustomerInfo(customerId);
                    notificationEntityService.logEntityAction(tenantId, reportId, savedReport, customerId, actionType, user, reportId.toString(), customerId.toString(), customerInfo.getTitle());
                }
                return savedReport;
            }
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.REPORT), actionType, user, e, reportId.toString());
            throw e;
        }
    }

    @Override
    public Report removeReportCustomers(Report report, Set<CustomerId> customerIds, User user) throws ThingsboardException {
        ActionType actionType = ActionType.UNASSIGNED_FROM_CUSTOMER;
        TenantId tenantId = report.getTenantId();
        ReportId reportId = report.getId();
        try {
            Set<CustomerId> removedCustomerIds = new HashSet<>();
            for (CustomerId customerId : customerIds) {
                if (report.isAssignedToCustomer(customerId)) {
                    removedCustomerIds.add(customerId);
                }
            }
            if (removedCustomerIds.isEmpty()) {
                return report;
            } else {
                Report savedReport = null;
                for (CustomerId customerId : removedCustomerIds) {
                    ShortCustomerInfo customerInfo = report.getAssignedCustomerInfo(customerId);
                    savedReport = checkNotNull(reportService.unassignReportFromCustomer(tenantId, reportId, customerId));
                    notificationEntityService.logEntityAction(tenantId, reportId, savedReport, customerId, actionType, user, reportId.toString(), customerId.toString(), customerInfo.getTitle());
                }
                return savedReport;
            }
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.REPORT), actionType, user, e, reportId.toString());
            throw e;
        }
    }
}
