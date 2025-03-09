package org.thingsboard.server.dao.sql.integration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.IntegrationEntity;
import org.thingsboard.server.dao.model.sql.IntegrationInfoEntity;
import org.thingsboard.server.dao.model.sql.UserEntity;

import java.util.List;
import java.util.UUID;

public interface IntegrationRepository extends JpaRepository<IntegrationEntity, UUID>, ExportableEntityRepository<IntegrationEntity> {

    @Query("SELECT new org.thingsboard.server.dao.model.sql.IntegrationInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM IntegrationEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.id = :integrationId")
    IntegrationInfoEntity findIntegrationInfoById(@Param("integrationId") UUID integrationId);

    @Query("SELECT a FROM IntegrationEntity a WHERE a.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<IntegrationEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                     @Param("textSearch") String textSearch,
                                     Pageable pageable);

    @Query("SELECT a FROM IntegrationEntity a WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<IntegrationEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                  @Param("customerId") UUID customerId,
                                                  @Param("textSearch") String textSearch,
                                                  Pageable pageable);

    List<IntegrationEntity> findByTenantIdAndIdIn(UUID tenantId, List<UUID> integrationIds);

    List<IntegrationEntity> findByTenantIdAndCustomerIdAndIdIn(UUID tenantId, UUID customerId, List<UUID> integrationIds);

    IntegrationEntity findByTenantIdAndName(UUID tenantId, String name);

    @Query("SELECT a FROM IntegrationEntity a WHERE a.tenantId = :tenantId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<IntegrationEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                            @Param("type") String type,
                                            @Param("textSearch") String textSearch,
                                            Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.IntegrationInfoEntity(a, c.title, c.additionalInfo) " +
            "FROM IntegrationEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<IntegrationInfoEntity> findIntegrationInfosByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                          @Param("type") String type,
                                                          @Param("textSearch") String textSearch,
                                                          Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.IntegrationInfoEntity(e, c.title, c.additionalInfo) " +
            "FROM IntegrationEntity e " +
            "LEFT JOIN CustomerEntity c on c.id = e.customerId " +
            "WHERE e.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(e.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<IntegrationInfoEntity> findIntegrationInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                             @Param("textSearch") String textSearch,
                                                             Pageable pageable);

    @Query("SELECT a FROM IntegrationEntity a WHERE " +
            "a.type = :type ")
    Page<IntegrationEntity> findIntegrations(@Param("type") String type,
                                             Pageable pageable);


    @Query("SELECT a FROM IntegrationEntity a WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<IntegrationEntity> findByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                         @Param("customerId") UUID customerId,
                                                         @Param("type") String type,
                                                         @Param("textSearch") String textSearch,
                                                         Pageable pageable);

    @Query("SELECT a FROM IntegrationEntity a, RelationEntity re WHERE a.tenantId = :tenantId " +
            "AND a.id = re.toId AND re.toType = 'INTEGRATION' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true)")
    Page<IntegrationEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                              @Param("edgeId") UUID edgeId,
                                              @Param("searchText") String searchText,
                                              Pageable pageable);

    @Query("SELECT a FROM IntegrationEntity a, RelationEntity re WHERE a.tenantId = :tenantId " +
            "AND a.id = re.toId AND re.toType = 'INTEGRATION' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND a.type = :type " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true)")
    Page<IntegrationEntity> findByTenantIdAndEdgeIdAndType(@Param("tenantId") UUID tenantId,
                                                     @Param("edgeId") UUID edgeId,
                                                     @Param("type") String type,
                                                     @Param("searchText") String searchText,
                                                     Pageable pageable);

    Long countByTenantIdAndTypeIsNot(UUID tenantId, String type);

    @Query("SELECT externalId FROM IntegrationEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);
}
