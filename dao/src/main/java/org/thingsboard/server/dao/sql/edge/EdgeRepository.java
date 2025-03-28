
package org.thingsboard.server.dao.sql.edge;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.EdgeEntity;
import org.thingsboard.server.dao.model.sql.EdgeInfoEntity;

import java.util.List;
import java.util.UUID;

public interface EdgeRepository extends JpaRepository<EdgeEntity, UUID> {

    @Query("SELECT d FROM EdgeEntity d WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<EdgeEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                 @Param("customerId") UUID customerId,
                                                 @Param("textSearch") String textSearch,
                                                 Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EdgeInfoEntity(d, c.title, c.additionalInfo) " +
            "FROM EdgeEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "WHERE d.id = :edgeId")
    EdgeInfoEntity findEdgeInfoById(@Param("edgeId") UUID edgeId);

    @Query("SELECT d FROM EdgeEntity d WHERE d.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<EdgeEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                    @Param("textSearch") String textSearch,
                                    Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EdgeInfoEntity(d, c.title, c.additionalInfo) " +
            "FROM EdgeEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "WHERE d.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<EdgeInfoEntity> findEdgeInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                 @Param("textSearch") String textSearch,
                                                 Pageable pageable);

    @Query("SELECT d FROM EdgeEntity d WHERE d.tenantId = :tenantId " +
            "AND d.type = :type " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<EdgeEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                           @Param("type") String type,
                                           @Param("textSearch") String textSearch,
                                           Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EdgeInfoEntity(d, c.title, c.additionalInfo) " +
            "FROM EdgeEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.type = :type " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<EdgeInfoEntity> findEdgeInfosByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                        @Param("type") String type,
                                                        @Param("textSearch") String textSearch,
                                                        Pageable pageable);

    @Query("SELECT d FROM EdgeEntity d WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND d.type = :type " +
            "AND (:textSearch IS NULL OR ilike(d.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<EdgeEntity> findByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                        @Param("customerId") UUID customerId,
                                                        @Param("type") String type,
                                                        @Param("textSearch") String textSearch,
                                                        Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EdgeInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM EdgeEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<EdgeInfoEntity> findEdgeInfosByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                                @Param("customerId") UUID customerId,
                                                                @Param("textSearch") String textSearch,
                                                                Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.EdgeInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM EdgeEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<EdgeInfoEntity> findEdgeInfosByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                                       @Param("customerId") UUID customerId,
                                                                       @Param("type") String type,
                                                                       @Param("textSearch") String textSearch,
                                                                       Pageable pageable);

    @Query("SELECT ee FROM EdgeEntity ee, RelationEntity re WHERE ee.tenantId = :tenantId " +
            "AND ee.id = re.fromId AND re.fromType = 'EDGE' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.toId = :entityId AND re.toType = :entityType " +
            "AND (:textSearch IS NULL OR ilike(ee.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<EdgeEntity> findByTenantIdAndEntityId(@Param("tenantId") UUID tenantId,
                                               @Param("entityId") UUID entityId,
                                               @Param("entityType") String entityType,
                                               @Param("textSearch") String textSearch,
                                               Pageable pageable);

    @Query("SELECT ee FROM EdgeEntity ee, TenantEntity te WHERE ee.tenantId = te.id AND te.tenantProfileId = :tenantProfileId ")
    Page<EdgeEntity> findByTenantProfileId(@Param("tenantProfileId") UUID tenantProfileId,
                                           Pageable pageable);

    @Query("SELECT DISTINCT d.type FROM EdgeEntity d WHERE d.tenantId = :tenantId")
    List<String> findTenantEdgeTypes(@Param("tenantId") UUID tenantId);

    EdgeEntity findByTenantIdAndName(UUID tenantId, String name);

    List<EdgeEntity> findEdgesByTenantIdAndCustomerIdAndIdIn(UUID tenantId, UUID customerId, List<UUID> edgeIds);

    List<EdgeEntity> findEdgesByTenantIdAndIdIn(UUID tenantId, List<UUID> edgeIds);

    EdgeEntity findByRoutingKey(String routingKey);
}
