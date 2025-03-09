package org.thingsboard.server.dao.sql.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Report;
import org.thingsboard.server.common.data.ReportInfo;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.ReportId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.ReportEntity;
import org.thingsboard.server.dao.report.ReportDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.Optional;
import java.util.UUID;

@Component
@SqlDao
public class JpaReportDao extends JpaAbstractDao<ReportEntity, Report> implements ReportDao {

    @Autowired
    ReportRepository reportRepository;

    @Override
    public Long countByTenantId(TenantId tenantId) {
        return reportRepository.countByTenantId(tenantId.getId());
    }

    @Override
    protected Class<ReportEntity> getEntityClass() {
        return ReportEntity.class;
    }

    @Override
    protected JpaRepository<ReportEntity, UUID> getRepository() {
        return reportRepository;
    }

    @Override
    public Report findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
//        return DaoUtil.getData(reportRepository.findByTenantIdAndExternalId(tenantId, externalId));
        return null;
    }

    @Override
    public PageData<Report> findByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(reportRepository.findByTenantId(tenantId, DaoUtil.toPageable(pageLink)));
    }

    @Override
    public ReportId getExternalIdByInternal(ReportId internalId) {
        return Optional.ofNullable(reportRepository.getExternalIdById(internalId.getId())).map(ReportId::new).orElse(null);
    }

    @Override
    public PageData<ReportId> findIdsByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(reportRepository.findIdsByTenantId(tenantId.getId(), DaoUtil.toPageable(pageLink)).map(ReportId::new));
    }

    @Override
    public PageData<ReportId> findAllIds(PageLink pageLink) {
        return DaoUtil.pageToPageData(reportRepository.findAllIds(DaoUtil.toPageable(pageLink)).map(ReportId::new));
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.REPORT;
    }
}
