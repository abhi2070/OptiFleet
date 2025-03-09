
package org.thingsboard.server.service.ttl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.notification.NotificationRequestConfig;
import org.thingsboard.server.dao.notification.NotificationRequestDao;
import org.thingsboard.server.dao.sqlts.insert.sql.SqlPartitioningRepository;
import org.thingsboard.server.queue.discovery.PartitionService;

import java.util.concurrent.TimeUnit;

import static org.thingsboard.server.dao.model.ModelConstants.NOTIFICATION_TABLE_NAME;

@Service
@ConditionalOnExpression("${sql.ttl.notifications.enabled:true} && ${sql.ttl.notifications.ttl:0} > 0")
@Slf4j
public class NotificationsCleanUpService extends AbstractCleanUpService {

    private final SqlPartitioningRepository partitioningRepository;
    private final NotificationRequestDao notificationRequestDao;

    @Value("${sql.ttl.notifications.ttl:2592000}")
    private long ttlInSec;
    @Value("${sql.notifications.partition_size:168}")
    private int partitionSizeInHours;

    public NotificationsCleanUpService(PartitionService partitionService, SqlPartitioningRepository partitioningRepository,
                                       NotificationRequestDao notificationRequestDao) {
        super(partitionService);
        this.partitioningRepository = partitioningRepository;
        this.notificationRequestDao = notificationRequestDao;
    }

    @Scheduled(initialDelayString = "#{T(org.apache.commons.lang3.RandomUtils).nextLong(0, ${sql.ttl.notifications.checking_interval_ms:86400000})}",
            fixedDelayString = "${sql.ttl.notifications.checking_interval_ms:86400000}")
    public void cleanUp() {
        long expTime = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(ttlInSec);
        long partitionDurationMs = TimeUnit.HOURS.toMillis(partitionSizeInHours);
        if (!isSystemTenantPartitionMine()) {
            partitioningRepository.cleanupPartitionsCache(NOTIFICATION_TABLE_NAME, expTime, partitionDurationMs);
            return;
        }

        long lastRemovedNotificationTs = partitioningRepository.dropPartitionsBefore(NOTIFICATION_TABLE_NAME, expTime, partitionDurationMs);
        if (lastRemovedNotificationTs > 0) {
            long gap = TimeUnit.MINUTES.toMillis(10);
            long requestExpTime = lastRemovedNotificationTs - TimeUnit.SECONDS.toMillis(NotificationRequestConfig.MAX_SENDING_DELAY) - gap;
            int removed = notificationRequestDao.removeAllByCreatedTimeBefore(requestExpTime);
            log.info("Removed {} outdated notification requests older than {}", removed, requestExpTime);
        }
    }

}
