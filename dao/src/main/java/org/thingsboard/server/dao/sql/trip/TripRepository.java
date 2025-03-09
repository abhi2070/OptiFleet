package org.thingsboard.server.dao.sql.trip;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.TripEntity;

import java.util.UUID;

public interface TripRepository extends JpaRepository<TripEntity, UUID>, ExportableEntityRepository<TripEntity> {

    Long countByTenantId(UUID tenantId);

    Page<TripEntity> findByTenantId(UUID tenantIs, Pageable pageable);

    @Query("SELECT tr FROM TripEntity tr WHERE tr.id = :tripId")
    TripEntity findTripInfoById(@Param("tripId") UUID tripId);

    @Query("SELECT tr FROM TripEntity tr WHERE tr.tenantId = :tenantId " +
            "AND (:searchText IS NULL OR ilike(tr.name, CONCAT('%', :searchText, '%')) = true)")
    Page<TripEntity> findByTenantId(@Param("tenantId") UUID tenantId, @Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT tr FROM TripEntity tr WHERE tr.name = :name")
    TripEntity findByTripName(@Param("name") String name);

}
