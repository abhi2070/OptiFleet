
package org.thingsboard.server.dao.eventsourcing;

import lombok.Builder;
import lombok.Data;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;

@Builder
@Data
public class SaveEntityEvent<T> {
    private final TenantId tenantId;
    private final T entity;
    private final T oldEntity;
    private final EntityId entityId;
    private final Boolean created;
}
