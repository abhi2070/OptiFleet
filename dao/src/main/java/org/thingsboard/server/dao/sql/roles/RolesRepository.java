package org.thingsboard.server.dao.sql.roles;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.RolesEntity;
import org.thingsboard.server.dao.model.sql.RolesInfoEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Utsav Kumar on 7/3/2024.
 */
public interface RolesRepository  extends JpaRepository<RolesEntity, UUID>, ExportableEntityRepository<RolesEntity> {


    @Query(" SELECT a From RolesEntity a"+
            " WHERE a.id = :rolesId ")
    RolesInfoEntity findRolesInfoById(@Param("rolesId") UUID rolesId);

    @Query(" SELECT a FROM RolesEntity a "+
            " WHERE a.tenantId = :tenantId " +
            " AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true) ")
    Page<RolesEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                     @Param("textSearch") String textSearch,
                                     Pageable pageable);



    @Query("SELECT r FROM RolesEntity r WHERE r.name = :name")
    RolesEntity findByName(@Param("name") String name);

    RolesEntity findByTenantIdAndName(UUID id, String name);

    @Query(" SELECT a FROM RolesEntity a WHERE a.tenantId = :tenantId " +
            " AND a.type = :type " +
            " AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true) ")
    Page<RolesEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                            @Param("type") String type,
                                            @Param("textSearch") String textSearch,
                                            Pageable pageable);

    @Query("SELECT a FROM RolesEntity a WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId AND a.type = :type " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<RolesEntity> findByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                         @Param("customerId") UUID customerId,
                                                         @Param("type") String type,
                                                         @Param("textSearch") String textSearch,
                                                         Pageable pageable);


    @Query("SELECT a FROM RolesEntity a WHERE a.tenantId = :tenantId " +
            "AND a.customerId = :customerId " +
            "AND (:textSearch IS NULL OR ilike(a.name, CONCAT('%', :textSearch, '%')) = true)")
    Page<RolesEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                  @Param("customerId") UUID customerId,
                                                  @Param("textSearch") String textSearch,
                                                  Pageable pageable);


    @Query("SELECT a FROM RolesEntity a, RelationEntity re WHERE a.tenantId = :tenantId " +
            "AND a.id = re.toId AND re.toType = 'ROLES' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true)")
    Page<RolesEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                              @Param("edgeId") UUID edgeId,
                                              @Param("searchText") String searchText,
                                              Pageable pageable);

    @Query("SELECT a FROM RolesEntity a, RelationEntity re WHERE a.tenantId = :tenantId " +
            "AND a.id = re.toId AND re.toType = 'ROLES' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND a.type = :type " +
            "AND (:searchText IS NULL OR ilike(a.name, CONCAT('%', :searchText, '%')) = true)")
    Page<RolesEntity> findByTenantIdAndEdgeIdAndType(@Param("tenantId") UUID tenantId,
                                                     @Param("edgeId") UUID edgeId,
                                                     @Param("type") String type,
                                                     @Param("searchText") String searchText,
                                                     Pageable pageable);

    Long countByTenantIdAndTypeIsNot(UUID tenantId, String type);

    @Query("SELECT externalId FROM RolesEntity WHERE id = :id")
    UUID getExternalIdById(@Param("id") UUID id);


}
