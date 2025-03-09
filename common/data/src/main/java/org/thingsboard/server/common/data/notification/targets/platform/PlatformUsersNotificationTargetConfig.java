
package org.thingsboard.server.common.data.notification.targets.platform;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.notification.targets.NotificationTargetConfig;
import org.thingsboard.server.common.data.notification.targets.NotificationTargetType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlatformUsersNotificationTargetConfig extends NotificationTargetConfig {

    @NotNull
    @Valid
    private UsersFilter usersFilter;

    @Override
    public NotificationTargetType getType() {
        return NotificationTargetType.PLATFORM_USERS;
    }

}
