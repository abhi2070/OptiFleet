
package org.thingsboard.server.dao.sql.dashboard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.DashboardInfoEntity;

import java.util.List;
import java.util.UUID;


public interface DashboardInfoRepository extends JpaRepository<DashboardInfoEntity, UUID> {

    DashboardInfoEntity findFirstByTenantIdAndTitle(UUID tenantId, String title);

    @Query("SELECT di FROM DashboardInfoEntity di WHERE di.tenantId = :tenantId " +
            "AND (:searchText IS NULL OR ilike(di.title, CONCAT('%', :searchText, '%')) = true)")
    Page<DashboardInfoEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                             @Param("searchText") String searchText,
                                             Pageable pageable);

    @Query("SELECT di FROM DashboardInfoEntity di WHERE di.tenantId = :tenantId " +
            "AND di.mobileHide = false " +
            "AND (:searchText IS NULL OR ilike(di.title, CONCAT('%', :searchText, '%')) = true)")
    Page<DashboardInfoEntity> findMobileByTenantId(@Param("tenantId") UUID tenantId,
                                                   @Param("searchText") String searchText,
                                                   Pageable pageable);

    @Query("SELECT di FROM DashboardInfoEntity di, RelationEntity re WHERE di.tenantId = :tenantId " +
            "AND di.id = re.toId AND re.toType = 'DASHBOARD' AND re.relationTypeGroup = 'DASHBOARD' " +
            "AND re.relationType = 'Contains' AND re.fromId = :customerId AND re.fromType = 'CUSTOMER' " +
            "AND (:searchText IS NULL OR ilike(di.title, CONCAT('%', :searchText, '%')) = true)")
    Page<DashboardInfoEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                          @Param("customerId") UUID customerId,
                                                          @Param("searchText") String searchText,
                                                          Pageable pageable);

    @Query("SELECT di FROM DashboardInfoEntity di, RelationEntity re WHERE di.tenantId = :tenantId " +
            "AND di.mobileHide = false " +
            "AND di.id = re.toId AND re.toType = 'DASHBOARD' AND re.relationTypeGroup = 'DASHBOARD' " +
            "AND re.relationType = 'Contains' AND re.fromId = :customerId AND re.fromType = 'CUSTOMER' " +
            "AND (:searchText IS NULL OR ilike(di.title, CONCAT('%', :searchText, '%')) = true)")
    Page<DashboardInfoEntity> findMobileByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                          @Param("customerId") UUID customerId,
                                                          @Param("searchText") String searchText,
                                                          Pageable pageable);

    @Query("SELECT di FROM DashboardInfoEntity di, RelationEntity re WHERE di.tenantId = :tenantId " +
            "AND di.id = re.toId AND re.toType = 'DASHBOARD' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND (:searchText IS NULL OR ilike(di.title, CONCAT('%', :searchText, '%')) = true)")
    Page<DashboardInfoEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                                      @Param("edgeId") UUID edgeId,
                                                      @Param("searchText") String searchText,
                                                      Pageable pageable);

    @Query("SELECT di.title FROM DashboardInfoEntity di WHERE di.tenantId = :tenantId AND di.id = :dashboardId")
    String findTitleByTenantIdAndId(@Param("tenantId") UUID tenantId, @Param("dashboardId") UUID dashboardId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM dashboard d WHERE d.tenant_id = :tenantId " +
                    "and (d.image = :imageLink or d.configuration ILIKE CONCAT('%\"', :imageLink, '\"%')) limit :lmt"
    )
    List<DashboardInfoEntity> findByTenantAndImageLink(@Param("tenantId") UUID tenantId, @Param("imageLink") String imageLink, @Param("lmt") int lmt);

    @Query(nativeQuery = true,
            value = "SELECT * FROM dashboard d WHERE d.image = :imageLink or d.configuration ILIKE CONCAT('%\"', :imageLink, '\"%') limit :lmt"
    )
    List<DashboardInfoEntity> findByImageLink(@Param("imageLink") String imageLink, @Param("lmt") int lmt);

}
