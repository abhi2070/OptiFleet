
package org.thingsboard.server.common.data.notification.rule.trigger;

import lombok.Builder;
import lombok.Data;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;

import java.util.concurrent.TimeUnit;

@Data
@Builder
public class EdgeConnectionTrigger implements NotificationRuleTrigger {

    private final TenantId tenantId;
    private final CustomerId customerId;
    private final EdgeId edgeId;
    private final boolean connected;
    private final String edgeName;

    @Override
    public boolean deduplicate() {
        return true;
    }

    @Override
    public String getDeduplicationKey() {
        return String.join(":", NotificationRuleTrigger.super.getDeduplicationKey(), String.valueOf(connected));
    }

    @Override
    public long getDefaultDeduplicationDuration() {
        return TimeUnit.MINUTES.toMillis(1);
    }

    @Override
    public NotificationRuleTriggerType getType() {
        return NotificationRuleTriggerType.EDGE_CONNECTION;
    }

    @Override
    public EntityId getOriginatorEntityId() {
        return edgeId;
    }
}
