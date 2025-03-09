
package org.thingsboard.server.cache.limits;

import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.id.TenantId;

public interface TenantProfileProvider {

    TenantProfile get(TenantId tenantId);

}
