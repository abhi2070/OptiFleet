
package org.thingsboard.server.dao;

import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.List;

public interface ImageContainerDao<T extends HasId<?>> {

    List<T> findByTenantAndImageLink(TenantId tenantId, String imageUrl, int limit);

    List<T> findByImageLink(String imageUrl, int limit);

}
