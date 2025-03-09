package org.thingsboard.server.dao.sql.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.ReportInfo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.ReportInfoEntity;
import org.thingsboard.server.dao.report.ReportInfoDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;


@Slf4j
@Component
@SqlDao
public class JpaReportInfoDao extends JpaAbstractDao<ReportInfoEntity, ReportInfo> implements ReportInfoDao {

    @Autowired
    private ReportInfoRepository reportInfoRepository;

    @Override
    protected Class<ReportInfoEntity> getEntityClass() {
        return ReportInfoEntity.class;
    }

    @Override
    protected JpaRepository<ReportInfoEntity, UUID> getRepository() {
        return reportInfoRepository;
    }

    @Override
    public PageData<ReportInfo> findReportsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(reportInfoRepository.findByTenantId(tenantId, pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public ReportInfo findReportInfoById(TenantId tenantId, UUID reportId) {
        return DaoUtil.getData(reportInfoRepository.findReportInfoById(reportId));
    }
}
