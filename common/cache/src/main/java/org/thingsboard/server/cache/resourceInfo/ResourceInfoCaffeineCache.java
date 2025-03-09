
package org.thingsboard.server.cache.resourceInfo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.CaffeineTbTransactionalCache;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.TbResourceInfo;


@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("ResourceInfoCache")
public class ResourceInfoCaffeineCache extends CaffeineTbTransactionalCache<ResourceInfoCacheKey, TbResourceInfo> {

    public ResourceInfoCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.RESOURCE_INFO_CACHE);
    }

}
