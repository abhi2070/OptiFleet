
package org.thingsboard.server.dao.attributes;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.thingsboard.server.cache.TbCacheValueWrapper;
import org.thingsboard.server.cache.TbTransactionalCache;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.stats.DefaultCounter;
import org.thingsboard.server.common.stats.StatsFactory;
import org.thingsboard.server.dao.cache.CacheExecutorService;
import org.thingsboard.server.dao.service.Validator;
import org.thingsboard.server.dao.sql.JpaExecutorService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.attributes.AttributeUtils.validate;

@Service
@ConditionalOnProperty(prefix = "cache.attributes", value = "enabled", havingValue = "true")
@Primary
@Slf4j
public class CachedAttributesService implements AttributesService {
    private static final String STATS_NAME = "attributes.cache";
    public static final String LOCAL_CACHE_TYPE = "caffeine";

    private final AttributesDao attributesDao;
    private final JpaExecutorService jpaExecutorService;
    private final CacheExecutorService cacheExecutorService;
    private final DefaultCounter hitCounter;
    private final DefaultCounter missCounter;
    private final TbTransactionalCache<AttributeCacheKey, AttributeKvEntry> cache;
    private ListeningExecutorService cacheExecutor;

    @Value("${cache.type:caffeine}")
    private String cacheType;
    @Value("${sql.attributes.value_no_xss_validation:false}")
    private boolean valueNoXssValidation;

    public CachedAttributesService(AttributesDao attributesDao,
                                   JpaExecutorService jpaExecutorService,
                                   StatsFactory statsFactory,
                                   CacheExecutorService cacheExecutorService,
                                   TbTransactionalCache<AttributeCacheKey, AttributeKvEntry> cache) {
        this.attributesDao = attributesDao;
        this.jpaExecutorService = jpaExecutorService;
        this.cacheExecutorService = cacheExecutorService;
        this.cache = cache;

        this.hitCounter = statsFactory.createDefaultCounter(STATS_NAME, "result", "hit");
        this.missCounter = statsFactory.createDefaultCounter(STATS_NAME, "result", "miss");
    }

    @PostConstruct
    public void init() {
        this.cacheExecutor = getExecutor(cacheType, cacheExecutorService);
    }

    /**
     * Will return:
     * - for the <b>local</b> cache type (cache.type="coffeine"): directExecutor (run callback immediately in the same thread)
     * - for the <b>remote</b> cache: dedicated thread pool for the cache IO calls to unblock any caller thread
     */
    ListeningExecutorService getExecutor(String cacheType, CacheExecutorService cacheExecutorService) {
        if (StringUtils.isEmpty(cacheType) || LOCAL_CACHE_TYPE.equals(cacheType)) {
            log.info("Going to use directExecutor for the local cache type {}", cacheType);
            return MoreExecutors.newDirectExecutorService();
        }
        log.info("Going to use cacheExecutorService for the remote cache type {}", cacheType);
        return cacheExecutorService.executor();
    }


    @Override
    public ListenableFuture<Optional<AttributeKvEntry>> find(TenantId tenantId, EntityId entityId, String scope, String attributeKey) {
        validate(entityId, scope);
        Validator.validateString(attributeKey, "Incorrect attribute key " + attributeKey);

        AttributeCacheKey attributeCacheKey = new AttributeCacheKey(scope, entityId, attributeKey);
        TbCacheValueWrapper<AttributeKvEntry> cachedAttributeValue = cache.get(attributeCacheKey);
        if (cachedAttributeValue != null) {
            hitCounter.increment();
            AttributeKvEntry cachedAttributeKvEntry = cachedAttributeValue.get();
            return Futures.immediateFuture(Optional.ofNullable(cachedAttributeKvEntry));
        } else {
            missCounter.increment();
            return cacheExecutor.submit(() -> {
                var cacheTransaction = cache.newTransactionForKey(attributeCacheKey);
                try {
                    Optional<AttributeKvEntry> result = attributesDao.find(tenantId, entityId, scope, attributeKey);
                    cacheTransaction.putIfAbsent(attributeCacheKey, result.orElse(null));
                    cacheTransaction.commit();
                    return result;
                } catch (Throwable e) {
                    cacheTransaction.rollback();
                    log.debug("Could not find attribute from cache: [{}] [{}] [{}]", entityId, scope, attributeKey, e);
                    throw e;
                }
            });
        }
    }

    @Override
    public ListenableFuture<List<AttributeKvEntry>> find(TenantId tenantId, EntityId entityId, String scope, final Collection<String> attributeKeysNonUnique) {
        validate(entityId, scope);
        final var attributeKeys = new LinkedHashSet<>(attributeKeysNonUnique); // deduplicate the attributes
        attributeKeys.forEach(attributeKey -> Validator.validateString(attributeKey, "Incorrect attribute key " + attributeKey));

        //CacheExecutor for Redis or DirectExecutor for local Caffeine
        return Futures.transformAsync(cacheExecutor.submit(() -> findCachedAttributes(entityId, scope, attributeKeys)),
                wrappedCachedAttributes -> {

        List<AttributeKvEntry> cachedAttributes = wrappedCachedAttributes.values().stream()
                .map(TbCacheValueWrapper::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (wrappedCachedAttributes.size() == attributeKeys.size()) {
            log.trace("[{}][{}] Found all attributes from cache: {}", entityId, scope, attributeKeys);
            return Futures.immediateFuture(cachedAttributes);
        }

        Set<String> notFoundAttributeKeys = new HashSet<>(attributeKeys);
        notFoundAttributeKeys.removeAll(wrappedCachedAttributes.keySet());

        List<AttributeCacheKey> notFoundKeys = notFoundAttributeKeys.stream().map(k -> new AttributeCacheKey(scope, entityId, k)).collect(Collectors.toList());

        // DB call should run in DB executor, not in cache-related executor
        return jpaExecutorService.submit(() -> {
            var cacheTransaction = cache.newTransactionForKeys(notFoundKeys);
            try {
                log.trace("[{}][{}] Lookup attributes from db: {}", entityId, scope, notFoundAttributeKeys);
                List<AttributeKvEntry> result = attributesDao.find(tenantId, entityId, scope, notFoundAttributeKeys);
                for (AttributeKvEntry foundInDbAttribute : result) {
                    AttributeCacheKey attributeCacheKey = new AttributeCacheKey(scope, entityId, foundInDbAttribute.getKey());
                    cacheTransaction.putIfAbsent(attributeCacheKey, foundInDbAttribute);
                    notFoundAttributeKeys.remove(foundInDbAttribute.getKey());
                }
                for (String key : notFoundAttributeKeys) {
                    cacheTransaction.putIfAbsent(new AttributeCacheKey(scope, entityId, key), null);
                }
                List<AttributeKvEntry> mergedAttributes = new ArrayList<>(cachedAttributes);
                mergedAttributes.addAll(result);
                cacheTransaction.commit();
                log.trace("[{}][{}] Commit cache transaction: {}", entityId, scope, notFoundAttributeKeys);
                return mergedAttributes;
            } catch (Throwable e) {
                cacheTransaction.rollback();
                log.debug("Could not find attributes from cache: [{}] [{}] [{}]", entityId, scope, notFoundAttributeKeys, e);
                throw e;
            }
        });

        }, MoreExecutors.directExecutor()); // cacheExecutor analyse and returns results or submit to DB executor
    }

    private Map<String, TbCacheValueWrapper<AttributeKvEntry>> findCachedAttributes(EntityId entityId, String scope, Collection<String> attributeKeys) {
        Map<String, TbCacheValueWrapper<AttributeKvEntry>> cachedAttributes = new HashMap<>();
        for (String attributeKey : attributeKeys) {
            var cachedAttributeValue = cache.get(new AttributeCacheKey(scope, entityId, attributeKey));
            if (cachedAttributeValue != null) {
                hitCounter.increment();
                cachedAttributes.put(attributeKey, cachedAttributeValue);
            } else {
                missCounter.increment();
            }
        }
        return cachedAttributes;
    }

    @Override
    public ListenableFuture<List<AttributeKvEntry>> findAll(TenantId tenantId, EntityId entityId, String scope) {
        validate(entityId, scope);
        return Futures.immediateFuture(attributesDao.findAll(tenantId, entityId, scope));
    }

    @Override
    public List<String> findAllKeysByDeviceProfileId(TenantId tenantId, DeviceProfileId deviceProfileId) {
        return attributesDao.findAllKeysByDeviceProfileId(tenantId, deviceProfileId);
    }

    @Override
    public List<String> findAllKeysByEntityIds(TenantId tenantId, EntityType entityType, List<EntityId> entityIds) {
        return attributesDao.findAllKeysByEntityIds(tenantId, entityType, entityIds);
    }

    @Override
    public List<String> findAllKeysByEntityIds(TenantId tenantId, EntityType entityType, List<EntityId> entityIds, String scope) {
        if (StringUtils.isEmpty(scope)) {
            return attributesDao.findAllKeysByEntityIds(tenantId, entityType, entityIds);
        } else {
            return attributesDao.findAllKeysByEntityIdsAndAttributeType(tenantId, entityType, entityIds, scope);
        }
    }

    @Override
    public ListenableFuture<String> save(TenantId tenantId, EntityId entityId, String scope, AttributeKvEntry attribute) {
        validate(entityId, scope);
        AttributeUtils.validate(attribute, valueNoXssValidation);
        ListenableFuture<String> future = attributesDao.save(tenantId, entityId, scope, attribute);
        return Futures.transform(future, key -> evict(entityId, scope, attribute, key), cacheExecutor);
    }

    @Override
    public ListenableFuture<List<String>> save(TenantId tenantId, EntityId entityId, String scope, List<AttributeKvEntry> attributes) {
        validate(entityId, scope);
        AttributeUtils.validate(attributes, valueNoXssValidation);

        List<ListenableFuture<String>> futures = new ArrayList<>(attributes.size());
        for (var attribute : attributes) {
            ListenableFuture<String> future = attributesDao.save(tenantId, entityId, scope, attribute);
            futures.add(Futures.transform(future, key -> evict(entityId, scope, attribute, key), cacheExecutor));
        }

        return Futures.allAsList(futures);
    }

    private String evict(EntityId entityId, String scope, AttributeKvEntry attribute, String key) {
        log.trace("[{}][{}][{}] Before cache evict: {}", entityId, scope, key, attribute);
        cache.evictOrPut(new AttributeCacheKey(scope, entityId, key), attribute);
        log.trace("[{}][{}][{}] after cache evict.", entityId, scope, key);
        return key;
    }

    @Override
    public ListenableFuture<List<String>> removeAll(TenantId tenantId, EntityId entityId, String scope, List<String> attributeKeys) {
        validate(entityId, scope);
        List<ListenableFuture<String>> futures = attributesDao.removeAll(tenantId, entityId, scope, attributeKeys);
        return Futures.allAsList(futures.stream().map(future -> Futures.transform(future, key -> {
            cache.evict(new AttributeCacheKey(scope, entityId, key));
            return key;
        }, cacheExecutor)).collect(Collectors.toList()));
    }

}
