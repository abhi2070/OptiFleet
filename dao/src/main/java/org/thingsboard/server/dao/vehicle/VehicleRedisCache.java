package org.thingsboard.server.dao.vehicle;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.CacheSpecsMap;
import org.thingsboard.server.cache.RedisTbTransactionalCache;
import org.thingsboard.server.cache.TBRedisCacheConfiguration;
import org.thingsboard.server.cache.TbFSTRedisSerializer;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.vehicle.Vehicle;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("VehicleCache")
public class VehicleRedisCache extends RedisTbTransactionalCache<VehicleCacheKey, Vehicle> {

    public VehicleRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.VEHICLE_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbFSTRedisSerializer<>());
    }
}
