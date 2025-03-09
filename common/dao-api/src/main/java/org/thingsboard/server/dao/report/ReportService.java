package org.thingsboard.server.dao.report;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.Report;
import org.thingsboard.server.common.data.ReportInfo;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

public interface ReportService extends EntityDaoService {

    ReportInfo findReportInfoById(TenantId tenantId, ReportId reportId);

    Report findReportById(TenantId tenantId, ReportId reportId);

    ListenableFuture<Report> findReportByIdAsync(TenantId tenantId, ReportId reportId);

    Report saveReport(Report report, boolean doValidate);

    Report saveReport(Report report);

    Report assignReportToCustomer(TenantId tenantId, ReportId reportId, CustomerId customerId);

    Report unassignReportFromCustomer(TenantId tenantId, ReportId reportId, CustomerId customerId);

    void deleteReport(TenantId tenantId, ReportId reportId);

    PageData<ReportInfo> findReportsByTenantId(TenantId tenantId, PageLink pageLink);
}
