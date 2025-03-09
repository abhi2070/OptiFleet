package org.thingsboard.server.dao.sql.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.ReportEntity;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<ReportEntity, UUID>, ExportableEntityRepository<ReportEntity> {

    Long countByTenantId(UUID tenantId);

    Page<ReportEntity> findByTenantId(UUID tenantId, Pageable pageable);

    @Query("SELECT externalId FROM ReportEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);

    @Query("SELECT r.id FROM ReportEntity r WHERE r.tenantId = :tenantId")
    Page<UUID> findIdsByTenantId(@Param("tenantId") UUID tenantId, Pageable pageable);

    @Query("SELECT r.id FROM ReportEntity r")
    Page<UUID> findAllIds(Pageable pageable);

}
