package org.thingsboard.server.dao.sql.data_converter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.data_converter.DataConverterInfo;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.DataConverterEntity;
import org.thingsboard.server.dao.model.sql.DataConverterInfoEntity;
import org.thingsboard.server.dao.model.sql.RolesEntity;

import java.util.List;
import java.util.UUID;

public interface DataConverterRepository extends JpaRepository<DataConverterEntity, UUID>, ExportableEntityRepository<DataConverterEntity> {

    @Query(value = "SELECT new org.thingsboard.server.dao.model.sql.DataConverterInfoEntity(a) " +
            "FROM DataConverterEntity a " +
            "WHERE a.id = :dataConverterId")
    DataConverterInfoEntity findDataConverterInfoById(@Param("dataConverterId") UUID dataConverterId);

    @Query("SELECT r FROM DataConverterEntity r WHERE r.name = :name AND r.type = :type")
    DataConverterEntity findByNameAndType(@Param("name") String name, @Param("type") String type);

    @Query("SELECT a FROM DataConverterEntity a WHERE a.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<DataConverterEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                             @Param("textSearch") String textSearch,
                                             Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DataConverterInfoEntity(a) " +
            "FROM DataConverterEntity a " +
            "WHERE a.tenantId = :tenantId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true  " +
            "  OR ilike(CONCAT('%', :textSearch, '%')) = true " +
            "  OR ilike( CONCAT('%', :textSearch, '%')) = true) ")
    Page<DataConverterInfoEntity> findDataConverterInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                                   @Param("textSearch") String textSearch,
                                                                   Pageable pageable);


    List<DataConverterEntity> findByTenantIdAndIdIn(UUID tenantId, List<UUID> dataConverterIds);

    DataConverterEntity findByTenantIdAndName(UUID tenantId, String name);

    @Query("SELECT a FROM DataConverterEntity a WHERE a.tenantId = :tenantId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<DataConverterEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                    @Param("type") String type,
                                                    @Param("textSearch") String textSearch,
                                                    Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DataConverterInfoEntity(a) " +
            "FROM DataConverterEntity a " +
            "WHERE a.tenantId = :tenantId " +
            "AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true  " +
            "  OR ilike(CONCAT('%', :textSearch, '%')) = true) ")
    Page<DataConverterInfoEntity> findDataConverterInfosByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                                          @Param("type") String type,
                                                                          @Param("textSearch") String textSearch,
                                                                          Pageable pageable);


    @Query("SELECT a FROM DataConverterEntity a, RelationEntity re WHERE a.tenantId = :tenantId " +
            "AND a.id = re.toId AND re.toType = 'DATACONVERTER' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true)")
    Page<DataConverterEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                                      @Param("edgeId") UUID edgeId,
                                                      @Param("searchText") String searchText,
                                                      Pageable pageable);

    @Query("SELECT a FROM DataConverterEntity a, RelationEntity re WHERE a.tenantId = :tenantId " +
            "AND a.id = re.toId AND re.toType = 'DATACONVERTER' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND a.type = :type " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true)")
    Page<DataConverterEntity> findByTenantIdAndEdgeIdAndType(@Param("tenantId") UUID tenantId,
                                                             @Param("edgeId") UUID edgeId,
                                                             @Param("type") String type,
                                                             @Param("searchText") String searchText,
                                                             Pageable pageable);

    Long countByTenantIdAndTypeIsNot(UUID tenantId, String type);

    @Query("SELECT externalId FROM DataConverterEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);

    @Query(value = "SELECT DISTINCT new org.thingsboard.server.common.data.util.TbPair(a.tenantId , a.type) FROM  DataConverterEntity a")
    Page<TbPair<UUID, String>> getAllDataConverterTypes(Pageable pageable);

    @Query("SELECT dc FROM DataConverterEntity dc WHERE dc.id = :converterId AND dc.type = :converterType")
    List<DataConverterEntity> findConverterDebugInfo(@Param("converterId") String converterId,
                                                     @Param("converterType") String converterType);



    @Query(value = "SELECT dc FROM DataConverterEntity dc WHERE dc.id = :converterId AND dc.type = :converterType")
    List<DataConverterEntity> findConverterDebugIn(@Param("converterId") UUID converterId,
                                                   @Param("converterType") String converterType);


    @Query("SELECT a.id, a.tenantId, a.name, a.type  FROM DataConverterEntity a WHERE a.tenantId = :tenantId AND (:textSearch IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<DataConverterInfo> findDataConvertInfos(@Param("tenantId") UUID tenantId, @Param("textSearch") String textSearch, Pageable pageable);


}

