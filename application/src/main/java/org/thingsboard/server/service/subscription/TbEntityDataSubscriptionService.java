
package org.thingsboard.server.service.subscription;

import org.thingsboard.server.service.ws.WebSocketSessionRef;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.AlarmCountCmd;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.AlarmDataCmd;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.EntityCountCmd;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.EntityDataCmd;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.UnsubscribeCmd;

public interface TbEntityDataSubscriptionService {

    void handleCmd(WebSocketSessionRef sessionId, EntityDataCmd cmd);

    void handleCmd(WebSocketSessionRef sessionId, EntityCountCmd cmd);

    void handleCmd(WebSocketSessionRef sessionId, AlarmDataCmd cmd);

    void handleCmd(WebSocketSessionRef sessionId, AlarmCountCmd cmd);

    void cancelSubscription(String sessionId, UnsubscribeCmd subscriptionId);

    void cancelAllSessionSubscriptions(String sessionId);

}
