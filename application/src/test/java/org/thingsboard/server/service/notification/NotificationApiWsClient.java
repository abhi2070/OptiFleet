
package org.thingsboard.server.service.notification;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.controller.TbTestWebSocketClient;
import org.thingsboard.server.service.ws.notification.cmd.MarkAllNotificationsAsReadCmd;
import org.thingsboard.server.service.ws.notification.cmd.MarkNotificationsAsReadCmd;
import org.thingsboard.server.service.ws.notification.cmd.NotificationsCountSubCmd;
import org.thingsboard.server.service.ws.notification.cmd.NotificationsSubCmd;
import org.thingsboard.server.service.ws.notification.cmd.UnreadNotificationsCountUpdate;
import org.thingsboard.server.service.ws.notification.cmd.UnreadNotificationsUpdate;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.CmdUpdateType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Getter
public class NotificationApiWsClient extends TbTestWebSocketClient {

    private UnreadNotificationsUpdate lastDataUpdate;
    private UnreadNotificationsCountUpdate lastCountUpdate;

    private int limit;
    private int unreadCount;
    private List<Notification> notifications;

    public NotificationApiWsClient(String wsUrl) throws URISyntaxException {
        super(new URI(wsUrl + "/api/ws"));
    }

    public NotificationApiWsClient subscribeForUnreadNotifications(int limit) {
        send(new NotificationsSubCmd(1, limit));
        this.limit = limit;
        return this;
    }

    public NotificationApiWsClient subscribeForUnreadNotificationsCount() {
        send(new NotificationsCountSubCmd(2));
        return this;
    }

    public void markNotificationAsRead(UUID... notifications) {
        send(new MarkNotificationsAsReadCmd(newCmdId(), Arrays.asList(notifications)));
    }

    public void markAllNotificationsAsRead() {
        send(new MarkAllNotificationsAsReadCmd(newCmdId()));
    }

    @Override
    public void registerWaitForUpdate(int count) {
        lastDataUpdate = null;
        lastCountUpdate = null;
        super.registerWaitForUpdate(count);
    }

    @Override
    public void onMessage(String s) {
        JsonNode update = JacksonUtil.toJsonNode(s);
        CmdUpdateType updateType = CmdUpdateType.valueOf(update.get("cmdUpdateType").asText());
        if (updateType == CmdUpdateType.NOTIFICATIONS) {
            lastDataUpdate = JacksonUtil.treeToValue(update, UnreadNotificationsUpdate.class);
            unreadCount = lastDataUpdate.getTotalUnreadCount();
            if (lastDataUpdate.getNotifications() != null) {
                notifications = new ArrayList<>(lastDataUpdate.getNotifications());
            } else {
                Notification notificationUpdate = lastDataUpdate.getUpdate();
                boolean updated = false;
                for (int i = 0; i < notifications.size(); i++) {
                    Notification existing = notifications.get(i);
                    if (existing.getId().equals(notificationUpdate.getId())) {
                        notifications.set(i, notificationUpdate);
                        updated = true;
                        break;
                    }
                }
                if (!updated) {
                    notifications.add(0, notificationUpdate);
                    if (notifications.size() > limit) {
                        notifications = notifications.subList(0, limit);
                    }
                }
            }
        } else if (updateType == CmdUpdateType.NOTIFICATIONS_COUNT) {
            lastCountUpdate = JacksonUtil.treeToValue(update, UnreadNotificationsCountUpdate.class);
            unreadCount = lastCountUpdate.getTotalUnreadCount();
        }
        super.onMessage(s);
    }

    private static int newCmdId() {
        return RandomUtils.nextInt(1, 1000);
    }

}
