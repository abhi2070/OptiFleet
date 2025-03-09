package org.thingsboard.server.dao.sql.vehicle;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.VehicleEntity;

import java.util.UUID;

public interface VehicleRepository extends JpaRepository<VehicleEntity, UUID>, ExportableEntityRepository<VehicleEntity> {

    @Query("SELECT vh from VehicleEntity vh WHERE vh.id = :vehicleId")
    VehicleEntity findVehicleInfoById(@Param("vehicleId") UUID vehicleId);

    @Query(" SELECT vh FROM VehicleEntity vh "+
            " WHERE vh.tenantId = :tenantId " +
            " AND (:textSearch IS NULL OR ilike(vh.vehiclenumber, CONCAT('%', :textSearch, '%')) = true) ")
    Page<VehicleEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                     @Param("textSearch") String textSearch,
                                     Pageable pageable);

    @Query("SELECT vh FROM VehicleEntity vh WHERE vh.vehiclenumber = :vehiclenumber")
    VehicleEntity findByVehiclenumber(@Param("vehiclenumber") String vehiclenumber);

    @Query(" SELECT vh FROM VehicleEntity vh WHERE vh.tenantId = :tenantId " +
            " AND vh.type = :type " +
            " AND (:textSearch IS NULL OR ilike(vh.vehiclenumber, CONCAT('%', :textSearch, '%')) = true) ")
    Page<VehicleEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                            @Param("type") String type,
                                            @Param("textSearch") String textSearch,
                                            Pageable pageable);

    Long countByTenantIdAndTypeIsNot(UUID tenantId, String type);

}
