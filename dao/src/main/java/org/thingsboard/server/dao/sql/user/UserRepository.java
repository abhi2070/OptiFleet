package org.thingsboard.server.dao.sql.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.model.sql.UserEntity;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Valerii Sosliuk
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    UserEntity findByEmail(String email);

    UserEntity findByTenantIdAndEmail(UUID tenantId, String email);

    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId " + "AND u.customerId = :customerId AND u.authority = :authority " + "AND (:searchText IS NULL OR ilike(u.email, CONCAT('%', :searchText, '%')) = true)")
    Page<UserEntity> findUsersByAuthority(@Param("tenantId") UUID tenantId, @Param("customerId") UUID customerId, @Param("searchText") String searchText, @Param("authority") Authority authority, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId " + "AND u.customerId IN (:customerIds) " + "AND (:searchText IS NULL OR ilike(u.email, CONCAT('%', :searchText, '%')) = true)")
    Page<UserEntity> findTenantAndCustomerUsers(@Param("tenantId") UUID tenantId, @Param("customerIds") Collection<UUID> customerIds, @Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId " + "AND (:searchText IS NULL OR ilike(u.email, CONCAT('%', :searchText, '%')) = true)")
    Page<UserEntity> findByTenantId(@Param("tenantId") UUID tenantId, @Param("searchText") String searchText, Pageable pageable);

    Page<UserEntity> findAllByAuthority(Authority authority, Pageable pageable);

    Page<UserEntity> findByAuthorityAndTenantIdIn(Authority authority, Collection<UUID> tenantsIds, Pageable pageable);

    @Query("SELECT u FROM UserEntity u INNER JOIN TenantEntity t ON u.tenantId = t.id AND u.authority = :authority " + "INNER JOIN TenantProfileEntity p ON t.tenantProfileId = p.id " + "WHERE p.id IN :profiles")
    Page<UserEntity> findByAuthorityAndTenantProfilesIds(@Param("authority") Authority authority, @Param("profiles") Collection<UUID> tenantProfilesIds, Pageable pageable);

    Long countByTenantId(UUID tenantId);

}
