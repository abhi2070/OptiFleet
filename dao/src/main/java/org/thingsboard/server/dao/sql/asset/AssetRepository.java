
package org.thingsboard.server.dao.sql.asset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.AssetEntity;
import org.thingsboard.server.dao.model.sql.AssetInfoEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 5/21/2017.
 */
public interface AssetRepository extends JpaRepository<AssetEntity, UUID>, ExportableEntityRepository<AssetEntity> {

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.id = :assetId")
    AssetInfoEntity findAssetInfoById(@Param("assetId") UUID assetId);

    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<AssetEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                     @Param("textSearch") String textSearch,
                                     Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true  " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(p.name, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(c.title, CONCAT('%', :textSearch, '%')) = true) ")
    Page<AssetInfoEntity> findAssetInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                   @Param("textSearch") String textSearch,
                                                   Pageable pageable);

    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<AssetEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                  @Param("customerId") UUID customerId,
                                                  @Param("textSearch") String textSearch,
                                                  Pageable pageable);

    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND a.assetProfileId = :profileId " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true)")
    Page<AssetEntity> findByTenantIdAndProfileId(@Param("tenantId") UUID tenantId,
                                                 @Param("profileId") UUID profileId,
                                                 @Param("searchText") String searchText,
                                                 Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true)")
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                                @Param("customerId") UUID customerId,
                                                                @Param("searchText") String searchText,
                                                                Pageable pageable);

    List<AssetEntity> findByTenantIdAndIdIn(UUID tenantId, List<UUID> assetIds);

    List<AssetEntity> findByTenantIdAndCustomerIdAndIdIn(UUID tenantId, UUID customerId, List<UUID> assetIds);

    AssetEntity findByTenantIdAndName(UUID tenantId, String name);

    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<AssetEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                            @Param("type") String type,
                                            @Param("textSearch") String textSearch,
                                            Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true  " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(c.title, CONCAT('%', :textSearch, '%')) = true) ")
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                          @Param("type") String type,
                                                          @Param("textSearch") String textSearch,
                                                          Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.assetProfileId = :assetProfileId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true  " +
            "  OR ilike(a.label, CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike(c.title, CONCAT('%', :textSearch, '%')) = true) ")
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndAssetProfileId(@Param("tenantId") UUID tenantId,
                                                                    @Param("assetProfileId") UUID assetProfileId,
                                                                    @Param("textSearch") String textSearch,
                                                                    Pageable pageable);


    @Query("SELECT a FROM AssetEntity a WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<AssetEntity> findByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                         @Param("customerId") UUID customerId,
                                                         @Param("type") String type,
                                                         @Param("textSearch") String textSearch,
                                                         Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                                       @Param("customerId") UUID customerId,
                                                                       @Param("type") String type,
                                                                       @Param("textSearch") String textSearch,
                                                                       Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInfoEntity(a, c.title, c.additionalInfo, p.name) " +
            "FROM AssetEntity a " +
            "LEFT JOIN CustomerEntity c on c.id = a.customerId " +
            "LEFT JOIN AssetProfileEntity p on p.id = a.assetProfileId " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND a.assetProfileId = :assetProfileId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<AssetInfoEntity> findAssetInfosByTenantIdAndCustomerIdAndAssetProfileId(@Param("tenantId") UUID tenantId,
                                                                                 @Param("customerId") UUID customerId,
                                                                                 @Param("assetProfileId") UUID assetProfileId,
                                                                                 @Param("textSearch") String textSearch,
                                                                                 Pageable pageable);

    Long countByAssetProfileId(UUID assetProfileId);

    @Query("SELECT a FROM AssetEntity a, RelationEntity re WHERE a.tenantId = :tenantId " +
            "AND a.id = re.toId AND re.toType = 'ASSET' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true)")
    Page<AssetEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                              @Param("edgeId") UUID edgeId,
                                              @Param("searchText") String searchText,
                                              Pageable pageable);

    @Query("SELECT a FROM AssetEntity a, RelationEntity re WHERE a.tenantId = :tenantId " +
            "AND a.id = re.toId AND re.toType = 'ASSET' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND a.type = :type " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true)")
    Page<AssetEntity> findByTenantIdAndEdgeIdAndType(@Param("tenantId") UUID tenantId,
                                                     @Param("edgeId") UUID edgeId,
                                                     @Param("type") String type,
                                                     @Param("searchText") String searchText,
                                                     Pageable pageable);

    Long countByTenantIdAndTypeIsNot(UUID tenantId, String type);

    @Query("SELECT externalId FROM AssetEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);

    @Query(value = "SELECT DISTINCT new org.thingsboard.server.common.data.util.TbPair(a.tenantId , a.type) FROM  AssetEntity a")
    Page<TbPair<UUID, String>> getAllAssetTypes(Pageable pageable);

}
