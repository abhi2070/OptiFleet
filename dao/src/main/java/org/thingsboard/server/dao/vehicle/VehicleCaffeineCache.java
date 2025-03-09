package org.thingsboard.server.dao.vehicle;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.CaffeineTbTransactionalCache;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.vehicle.Vehicle;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("VehicleCache")
public class VehicleCaffeineCache extends CaffeineTbTransactionalCache<VehicleCacheKey, Vehicle> {

    public VehicleCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.VEHICLE_CACHE);
    }
}
