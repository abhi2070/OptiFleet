
package org.thingsboard.server.service.ws.notification.sub;

import lombok.Builder;
import lombok.Getter;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.service.subscription.TbSubscription;
import org.thingsboard.server.service.subscription.TbSubscriptionType;
import org.thingsboard.server.service.ws.notification.cmd.UnreadNotificationsUpdate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Getter
public class NotificationsSubscription extends AbstractNotificationSubscription<NotificationsSubscriptionUpdate> {

    private final Map<UUID, Notification> latestUnreadNotifications = new HashMap<>();
    private final int limit;

    @Builder
    public NotificationsSubscription(String serviceId, String sessionId, int subscriptionId, TenantId tenantId, EntityId entityId,
                                     BiConsumer<TbSubscription<NotificationsSubscriptionUpdate>, NotificationsSubscriptionUpdate> updateProcessor,
                                     int limit) {
        super(serviceId, sessionId, subscriptionId, tenantId, entityId, TbSubscriptionType.NOTIFICATIONS, updateProcessor);
        this.limit = limit;
    }

    public UnreadNotificationsUpdate createFullUpdate() {
        return UnreadNotificationsUpdate.builder()
                .cmdId(getSubscriptionId())
                .notifications(getSortedNotifications())
                .totalUnreadCount(totalUnreadCounter.get())
                .sequenceNumber(sequence.incrementAndGet())
                .build();
    }

    public List<Notification> getSortedNotifications() {
        return latestUnreadNotifications.values().stream()
                .sorted(Comparator.comparing(BaseData::getCreatedTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public UnreadNotificationsUpdate createPartialUpdate(Notification notification) {
        return UnreadNotificationsUpdate.builder()
                .cmdId(getSubscriptionId())
                .update(notification)
                .totalUnreadCount(totalUnreadCounter.get())
                .sequenceNumber(sequence.incrementAndGet())
                .build();
    }

    public UnreadNotificationsUpdate createCountUpdate() {
        return UnreadNotificationsUpdate.builder()
                .cmdId(getSubscriptionId())
                .totalUnreadCount(totalUnreadCounter.get())
                .sequenceNumber(sequence.incrementAndGet())
                .build();
    }

}
