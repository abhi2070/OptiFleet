
package org.thingsboard.server.dao.eventsourcing;

import lombok.Builder;
import lombok.Data;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;

@Data
@Builder
public class ActionEntityEvent {
    private final TenantId tenantId;
    private final EdgeId edgeId;
    private final EntityId entityId;
    private final String body;
    private final ActionType actionType;
}
