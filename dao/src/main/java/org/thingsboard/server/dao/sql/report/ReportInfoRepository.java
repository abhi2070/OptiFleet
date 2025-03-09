package org.thingsboard.server.dao.sql.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.ReportInfoEntity;

import java.util.UUID;

public interface ReportInfoRepository extends JpaRepository<ReportInfoEntity, UUID> {

    @Query("SELECT ri FROM ReportInfoEntity ri WHERE ri.tenantId = :tenantId " + "AND (:searchText IS NULL OR ilike(ri.name, CONCAT('%', :searchText, '%')) = true)")
    Page<ReportInfoEntity> findByTenantId(@Param("tenantId") UUID tenantId, @Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT ri FROM ReportInfoEntity ri WHERE ri.id = :reportId ")
    ReportInfoEntity findReportInfoById(@Param("reportId") UUID reportId);

}
