package org.thingsboard.server.dao.data_converter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.*;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.data_converter.DataConverter;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("DataConverterCache")
public class DataConverterRedisCache extends RedisTbTransactionalCache<DataConverterCacheKey, DataConverter> {

    public DataConverterRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.DATACONVERTER_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbFSTRedisSerializer<>());
    }
}

