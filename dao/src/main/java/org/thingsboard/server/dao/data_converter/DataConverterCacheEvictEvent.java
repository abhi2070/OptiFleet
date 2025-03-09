package org.thingsboard.server.dao.data_converter;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.thingsboard.server.common.data.id.TenantId;

@Data
@RequiredArgsConstructor
public class DataConverterCacheEvictEvent {
    private final TenantId tenantId;
    private final String newName;
    private final String oldName;
}

