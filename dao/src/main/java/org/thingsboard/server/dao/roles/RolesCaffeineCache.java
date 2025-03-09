package org.thingsboard.server.dao.roles;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.CaffeineTbTransactionalCache;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.roles.Roles;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("RolesCache")
public class RolesCaffeineCache extends CaffeineTbTransactionalCache<RolesCacheKey, Roles> {
    public RolesCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.ROLES_CACHE);
    }
}
