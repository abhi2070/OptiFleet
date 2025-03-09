package org.thingsboard.server.dao.scheduler;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.CacheSpecsMap;
import org.thingsboard.server.cache.RedisTbTransactionalCache;
import org.thingsboard.server.cache.TBRedisCacheConfiguration;
import org.thingsboard.server.cache.TbFSTRedisSerializer;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.dao.asset.AssetCacheKey;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("SchedulerCache")
public class SchedulerRedisCache extends RedisTbTransactionalCache<SchedulerCacheKey, Scheduler> {
    public SchedulerRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.SCHEDULER_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbFSTRedisSerializer<>());
    }


}
