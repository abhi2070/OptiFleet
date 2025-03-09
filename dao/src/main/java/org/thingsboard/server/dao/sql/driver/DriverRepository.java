package org.thingsboard.server.dao.sql.driver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.DriverEntity;

import java.util.UUID;

public interface DriverRepository extends JpaRepository<DriverEntity, UUID>, ExportableEntityRepository<DriverEntity> {

    Long countByTenantId(UUID tenantId);

    Page<DriverEntity> findByTenantId(UUID tenantId, Pageable pageable);

    @Query("SELECT dr FROM DriverEntity dr WHERE dr.id = :driverId")
    DriverEntity findDriverInfoById(@Param("driverId") UUID driverId);

    @Query("SELECT dr FROM DriverEntity dr WHERE dr.tenantId = :tenantId " +
            "AND (:searchText IS NULL OR ilike(dr.name, CONCAT('%', :searchText, '%')) = true)")
    Page<DriverEntity> findByTenantId(@Param("tenantId") UUID tenantId, @Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT dr FROM DriverEntity dr WHERE dr.drivingLicenseNumber = :drivingLicenseNumber")
    DriverEntity findByDriverLicenseNumber(@Param("drivingLicenseNumber") String drivingLicenseNumber);

}
