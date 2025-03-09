package org.thingsboard.server.dao.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.CaffeineTbTransactionalCache;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.scheduler.Scheduler;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("SchedulerCache")

public class SchedulerCaffeineCache extends CaffeineTbTransactionalCache<SchedulerCacheKey, Scheduler> {

    public SchedulerCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.SCHEDULER_CACHE);
    }

}