package org.thingsboard.server.dao.sql.scheduler;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.RolesEntity;
import org.thingsboard.server.dao.model.sql.SchedulerEntity;
import org.thingsboard.server.dao.model.sql.SchedulerInfoEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Scheduler repository interface for CRUD operations and custom queries.
 */
@Repository
public interface SchedulerRepository extends JpaRepository<SchedulerEntity, UUID>, ExportableEntityRepository<SchedulerEntity> {

    @Query("SELECT s FROM SchedulerEntity s WHERE s.id = :schedulerId")
    Optional<SchedulerEntity> findById(@Param("schedulerId") UUID schedulerId);

    @Query("SELECT s FROM SchedulerEntity s WHERE s.toAddress = :toAddress")
    SchedulerEntity findByEmail(@Param("toAddress") String toAddress);

    @Query("SELECT s FROM SchedulerEntity s WHERE s.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<SchedulerEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                         @Param("textSearch") String textSearch,
                                         Pageable pageable);

    SchedulerEntity findByTenantIdAndName(UUID tenantId, String name);

    @Query("SELECT s.externalId FROM SchedulerEntity s WHERE s.id = :id")
    UUID getExternalIdById(@Param("id") UUID id);

    @Query("SELECT s FROM SchedulerEntity s WHERE s.id = :schedulerId")
    SchedulerInfoEntity findSchedulerInfoById(@Param("schedulerId") UUID schedulerId);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.SchedulerInfoEntity(s) " +
            "FROM SchedulerEntity s " +
            "WHERE s.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<SchedulerInfoEntity> findSchedulerInfosByTenant(@Param("tenantId") UUID tenantId,

                                                                  @Param("textSearch") String textSearch,
                                                                  Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.SchedulerInfoEntity(s) " +
            "FROM SchedulerEntity s " +
            "WHERE s.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<SchedulerInfoEntity> findSchedulerInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                           @Param("textSearch") String textSearch,
                                                           Pageable pageable);

//    @Query("SELECT s FROM SchedulerInfoEntity s WHERE s.tenantId = :tenantId")
//    SchedulerInfoEntity findSchedulerInfosByTenantIdTest(@Param("tenantId") UUID tenantId);

//    @Query("SELECT s FROM SchedulerEntity s WHERE s.tenantId = :tenantId " +
//            "AND (:textSearch IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
//    Page<SchedulerEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
//                                                @Param("type") String type,
//                                                @Param("textSearch") String textSearch,
//                                                Pageable pageable);
}
