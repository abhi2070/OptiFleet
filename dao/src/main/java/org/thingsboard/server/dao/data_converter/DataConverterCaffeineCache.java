package org.thingsboard.server.dao.data_converter;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.CaffeineTbTransactionalCache;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.data_converter.DataConverter;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("DataConverterCache")
public class DataConverterCaffeineCache extends CaffeineTbTransactionalCache<DataConverterCacheKey, DataConverter> {


    public DataConverterCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.DATACONVERTER_CACHE);
    }
}

