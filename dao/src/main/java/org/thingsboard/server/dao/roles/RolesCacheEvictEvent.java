package org.thingsboard.server.dao.roles;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.thingsboard.server.common.data.id.TenantId;

@Data
@RequiredArgsConstructor
public class RolesCacheEvictEvent {
    private final TenantId tenantId;
    private final String newName;
    private final String oldName;

}
