
package org.thingsboard.server.cache.resourceInfo;

import lombok.Data;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;

@Data
public class ResourceInfoEvictEvent {
    private final TenantId tenantId;
    private final TbResourceId resourceId;
}
