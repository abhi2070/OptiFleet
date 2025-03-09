package org.thingsboard.server.dao.report;

import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.Report;
import org.thingsboard.server.common.data.ReportInfo;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.ReportId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;
import java.util.UUID;

public interface ReportDao extends Dao<Report>, TenantEntityDao, ExportableEntityDao<ReportId, Report> {

    /**
     * Save or update report object
     *
     * @param report the report object
     * @return saved report object
     */
    Report save(TenantId tenantId, Report report);

    PageData<ReportId> findIdsByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<ReportId> findAllIds(PageLink pageLink);

}
