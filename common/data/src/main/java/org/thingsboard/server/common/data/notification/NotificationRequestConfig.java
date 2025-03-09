
package org.thingsboard.server.common.data.notification;

import lombok.Data;

import javax.validation.constraints.Max;

@Data
public class NotificationRequestConfig {

    @Max(value = MAX_SENDING_DELAY, message = "cannot be longer than 1 week")
    private int sendingDelayInSec;

    public static final int MAX_SENDING_DELAY = 604800;

}
