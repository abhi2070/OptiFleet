
package org.thingsboard.server.service.ws.telemetry.cmd.v2;

import lombok.Data;
import org.thingsboard.server.service.ws.WsCmdType;

@Data
public class EntityCountUnsubscribeCmd implements UnsubscribeCmd {

    private final int cmdId;

    @Override
    public WsCmdType getType() {
        return WsCmdType.ENTITY_COUNT_UNSUBSCRIBE;
    }
}
