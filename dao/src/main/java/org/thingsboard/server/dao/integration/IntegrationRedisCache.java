package org.thingsboard.server.dao.integration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.CacheSpecsMap;
import org.thingsboard.server.cache.RedisTbTransactionalCache;
import org.thingsboard.server.cache.TBRedisCacheConfiguration;
import org.thingsboard.server.cache.TbFSTRedisSerializer;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.integration.Integration;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("IntegrationCache")
public class IntegrationRedisCache extends RedisTbTransactionalCache<IntegrationCacheKey, Integration> {

    public IntegrationRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.INTEGRATION_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbFSTRedisSerializer<>());
    }
}
