package org.thingsboard.server.dao.vehicle;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.thingsboard.server.common.data.id.TenantId;

@Data
@RequiredArgsConstructor
public class VehicleCacheEvictEvent {
    private final TenantId tenantId;
    private final String newNumber;
    private final String oldNumber;
}
