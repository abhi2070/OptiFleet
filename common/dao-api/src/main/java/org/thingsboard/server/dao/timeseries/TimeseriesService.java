
package org.thingsboard.server.dao.timeseries;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.DeleteTsKvQuery;
import org.thingsboard.server.common.data.kv.ReadTsKvQuery;
import org.thingsboard.server.common.data.kv.ReadTsKvQueryResult;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.kv.TsKvLatestRemovingResult;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Andrew Shvayka
 */
public interface TimeseriesService {

    ListenableFuture<List<ReadTsKvQueryResult>> findAllByQueries(TenantId tenantId, EntityId entityId, List<ReadTsKvQuery> queries);

    ListenableFuture<List<TsKvEntry>> findAll(TenantId tenantId, EntityId entityId, List<ReadTsKvQuery> queries);

    ListenableFuture<Optional<TsKvEntry>> findLatest(TenantId tenantId, EntityId entityId, String keys);

    ListenableFuture<List<TsKvEntry>> findLatest(TenantId tenantId, EntityId entityId, Collection<String> keys);

    List<TsKvEntry> findLatestSync(TenantId tenantId, EntityId entityId, Collection<String> keys);

    ListenableFuture<List<TsKvEntry>> findAllLatest(TenantId tenantId, EntityId entityId);

    ListenableFuture<Integer> save(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry);

    ListenableFuture<Integer> save(TenantId tenantId, EntityId entityId, List<TsKvEntry> tsKvEntry, long ttl);

    ListenableFuture<Integer> saveWithoutLatest(TenantId tenantId, EntityId entityId, List<TsKvEntry> tsKvEntry, long ttl);

    ListenableFuture<List<Void>> saveLatest(TenantId tenantId, EntityId entityId, List<TsKvEntry> tsKvEntry);

    ListenableFuture<List<TsKvLatestRemovingResult>> remove(TenantId tenantId, EntityId entityId, List<DeleteTsKvQuery> queries);

    ListenableFuture<List<TsKvLatestRemovingResult>> removeLatest(TenantId tenantId, EntityId entityId, Collection<String> keys);

    ListenableFuture<Collection<String>> removeAllLatest(TenantId tenantId, EntityId entityId);

    List<String> findAllKeysByDeviceProfileId(TenantId tenantId, DeviceProfileId deviceProfileId);

    List<String> findAllKeysByEntityIds(TenantId tenantId, List<EntityId> entityIds);

    void cleanup(long systemTtl);
}
