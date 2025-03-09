package org.thingsboard.server.dao.report;

import org.thingsboard.server.common.data.ReportInfo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.UUID;

public interface ReportInfoDao extends Dao<ReportInfo> {

    /**
     * Find reports by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of report objects
     */
    PageData<ReportInfo> findReportsByTenantId(UUID tenantId, PageLink pageLink);

    /**
     * Find report info by id.
     *
     * @param tenantId the tenant id
     * @param reportId the report id
     * @return the report info object
     */
    ReportInfo findReportInfoById(TenantId tenantId, UUID reportId);
}
