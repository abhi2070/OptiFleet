
package org.thingsboard.server.service.ws.notification.sub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.common.data.notification.NotificationStatus;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationUpdate {

    private UUID notificationId;

    private boolean created;
    private Notification notification;

    private boolean updated;
    private NotificationStatus newStatus;
    private boolean allNotifications;

    private boolean deleted;

}
