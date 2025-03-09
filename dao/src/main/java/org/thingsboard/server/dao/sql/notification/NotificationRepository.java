
package org.thingsboard.server.dao.sql.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.notification.NotificationStatus;
import org.thingsboard.server.dao.model.sql.NotificationEntity;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    @Query("SELECT n FROM NotificationEntity n WHERE n.recipientId = :recipientId AND n.status <> :status " +
            "AND (:searchText is NULL OR ilike(n.subject, concat('%', :searchText, '%')) = true " +
            "OR ilike(n.text, concat('%', :searchText, '%')) = true)")
    Page<NotificationEntity> findByRecipientIdAndStatusNot(@Param("recipientId") UUID recipientId,
                                                           @Param("status") NotificationStatus status,
                                                           @Param("searchText") String searchText,
                                                           Pageable pageable);

    @Query("SELECT n FROM NotificationEntity n WHERE n.recipientId = :recipientId " +
            "AND (:searchText is NULL OR ilike(n.subject, concat('%', :searchText, '%')) = true " +
            "OR ilike(n.text, concat('%', :searchText, '%')) = true)")
    Page<NotificationEntity> findByRecipientId(@Param("recipientId") UUID recipientId,
                                               @Param("searchText") String searchText,
                                               Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationEntity n SET n.status = :status " +
            "WHERE n.id = :id AND n.recipientId = :recipientId AND n.status <> :status")
    int updateStatusByIdAndRecipientId(@Param("id") UUID id,
                                       @Param("recipientId") UUID recipientId,
                                       @Param("status") NotificationStatus status);

    int countByRecipientIdAndStatusNot(UUID recipientId, NotificationStatus status);

    Page<NotificationEntity> findByRequestId(UUID requestId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.id = :id AND n.recipientId = :recipientId")
    int deleteByIdAndRecipientId(@Param("id") UUID id, @Param("recipientId") UUID recipientId);

    @Transactional
    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.requestId = :requestId")
    void deleteByRequestId(@Param("requestId") UUID requestId);

    @Transactional
    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.recipientId = :recipientId")
    void deleteByRecipientId(@Param("recipientId") UUID recipientId);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationEntity n SET n.status = :status " +
            "WHERE n.recipientId = :recipientId AND n.status <> :status")
    int updateStatusByRecipientId(@Param("recipientId") UUID recipientId,
                                  @Param("status") NotificationStatus status);

}
