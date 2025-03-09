
package org.thingsboard.server.cache.resourceInfo;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class ResourceInfoCacheKey implements Serializable {

    private final TenantId tenantId;
    private final TbResourceId tbResourceId;

    @Override
    public String toString() {
        return tenantId + "_" + tbResourceId;
    }
}
