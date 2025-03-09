
package org.thingsboard.server.dao.alarm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.CacheSpecsMap;
import org.thingsboard.server.cache.RedisTbTransactionalCache;
import org.thingsboard.server.cache.TBRedisCacheConfiguration;
import org.thingsboard.server.cache.TbFSTRedisSerializer;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("AlarmTypesCache")
public class AlarmTypesRedisCache extends RedisTbTransactionalCache<TenantId, PageData<EntitySubtype>> {

    public AlarmTypesRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.ALARM_TYPES_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbFSTRedisSerializer<>());
    }
}
