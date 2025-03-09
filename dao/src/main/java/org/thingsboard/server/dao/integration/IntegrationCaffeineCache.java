package org.thingsboard.server.dao.integration;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.CaffeineTbTransactionalCache;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.integration.Integration;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("IntegrationCache")
public class IntegrationCaffeineCache extends CaffeineTbTransactionalCache<IntegrationCacheKey, Integration> {

    public IntegrationCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.INTEGRATION_CACHE);
    }
}
