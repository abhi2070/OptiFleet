
package org.thingsboard.server.service.entity.tenant;

import org.thingsboard.server.common.data.Tenant;

public interface TbTenantService {

    Tenant save(Tenant tenant) throws Exception;

    void delete(Tenant tenant) throws Exception;

}
