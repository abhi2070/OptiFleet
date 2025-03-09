
package org.thingsboard.server.dao.sql.notification;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.notification.NotificationRequestStatus;
import org.thingsboard.server.dao.model.sql.NotificationRequestEntity;
import org.thingsboard.server.dao.model.sql.NotificationRequestInfoEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRequestRepository extends JpaRepository<NotificationRequestEntity, UUID> {

    String REQUEST_INFO_QUERY = "SELECT new org.thingsboard.server.dao.model.sql.NotificationRequestInfoEntity(r, t.name, t.configuration) " +
            "FROM NotificationRequestEntity r LEFT JOIN NotificationTemplateEntity t ON r.templateId = t.id";

    Page<NotificationRequestEntity> findByTenantIdAndOriginatorEntityType(UUID tenantId, EntityType originatorType, Pageable pageable);

    @Query(REQUEST_INFO_QUERY + " WHERE r.tenantId = :tenantId AND r.originatorEntityType = :originatorType " +
            "AND (:searchText is NULL OR (t.name IS NOT NULL AND ilike(t.name, concat('%', :searchText, '%')) = true))")
    Page<NotificationRequestInfoEntity> findInfosByTenantIdAndOriginatorEntityTypeAndSearchText(@Param("tenantId") UUID tenantId,
                                                                                                @Param("originatorType") EntityType originatorType,
                                                                                                @Param("searchText") String searchText,
                                                                                                Pageable pageable);

    @Query(REQUEST_INFO_QUERY + " WHERE r.id = :id")
    NotificationRequestInfoEntity findInfoById(@Param("id") UUID id);

    @Query("SELECT r.id FROM NotificationRequestEntity r WHERE r.status = :status AND r.ruleId = :ruleId")
    List<UUID> findAllIdsByStatusAndRuleId(@Param("status") NotificationRequestStatus status,
                                           @Param("ruleId") UUID ruleId);

    List<NotificationRequestEntity> findAllByRuleIdAndOriginatorEntityIdAndOriginatorEntityType(UUID ruleId, UUID originatorEntityId, EntityType originatorEntityType);

    Page<NotificationRequestEntity> findAllByStatus(NotificationRequestStatus status, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationRequestEntity r SET r.status = :status, r.stats = :stats WHERE r.id = :id")
    void updateStatusAndStatsById(@Param("id") UUID id,
                                  @Param("status") NotificationRequestStatus status,
                                  @Param("stats") JsonNode stats);

    boolean existsByTenantIdAndStatusAndTargetsContaining(UUID tenantId, NotificationRequestStatus status, String targetIdStr);

    boolean existsByTenantIdAndStatusAndTemplateId(UUID tenantId, NotificationRequestStatus status, UUID templateId);

    @Transactional
    @Modifying
    @Query("DELETE FROM NotificationRequestEntity r WHERE r.createdTime < :ts")
    int deleteAllByCreatedTimeBefore(@Param("ts") long ts);

    @Transactional
    @Modifying
    @Query("DELETE FROM NotificationRequestEntity r WHERE r.tenantId = :tenantId")
    void deleteByTenantId(@Param("tenantId") UUID tenantId);

}
