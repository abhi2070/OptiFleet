
package org.thingsboard.rule.engine.util;

import com.google.common.util.concurrent.Futures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thingsboard.common.util.ListeningExecutor;
import org.thingsboard.rule.engine.TestDbCallbackExecutor;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.data.DeviceRelationsQuery;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.device.DeviceSearchQuery;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.common.data.relation.RelationsSearchParameters;
import org.thingsboard.server.dao.device.DeviceService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EntitiesRelatedDeviceIdAsyncLoaderTest {

    private static final EntityId DUMMY_ORIGINATOR = new DeviceId(UUID.randomUUID());
    private static final TenantId TENANT_ID = new TenantId(UUID.randomUUID());
    private static final ListeningExecutor DB_EXECUTOR = new TestDbCallbackExecutor();
    @Mock
    private TbContext ctxMock;
    @Mock
    private DeviceService deviceServiceMock;

    @Test
    public void givenDeviceRelationsQuery_whenFindDeviceAsync_ShouldBuildCorrectDeviceSearchQuery() {
        // GIVEN
        var deviceRelationsQuery = new DeviceRelationsQuery();
        deviceRelationsQuery.setDeviceTypes(List.of("Device type 1", "Device type 2", "default"));
        deviceRelationsQuery.setDirection(EntitySearchDirection.FROM);
        deviceRelationsQuery.setMaxLevel(2);
        deviceRelationsQuery.setRelationType(EntityRelation.CONTAINS_TYPE);

        var expectedDeviceSearchQuery = new DeviceSearchQuery();
        var parameters = new RelationsSearchParameters(
                DUMMY_ORIGINATOR,
                deviceRelationsQuery.getDirection(),
                deviceRelationsQuery.getMaxLevel(),
                deviceRelationsQuery.isFetchLastLevelOnly()
        );
        expectedDeviceSearchQuery.setParameters(parameters);
        expectedDeviceSearchQuery.setRelationType(deviceRelationsQuery.getRelationType());
        expectedDeviceSearchQuery.setDeviceTypes(deviceRelationsQuery.getDeviceTypes());

        when(ctxMock.getTenantId()).thenReturn(TENANT_ID);
        when(ctxMock.getDeviceService()).thenReturn(deviceServiceMock);
        when(deviceServiceMock.findDevicesByQuery(eq(TENANT_ID), eq(expectedDeviceSearchQuery)))
                .thenReturn(Futures.immediateFuture(null));
        when(ctxMock.getDbCallbackExecutor()).thenReturn(DB_EXECUTOR);

        // WHEN
        EntitiesRelatedDeviceIdAsyncLoader.findDeviceAsync(ctxMock, DUMMY_ORIGINATOR, deviceRelationsQuery);

        // THEN
        verify(deviceServiceMock, times(1)).findDevicesByQuery(eq(TENANT_ID), eq(expectedDeviceSearchQuery));
    }

    @Test
    public void givenSeveralDevicesFound_whenFindDeviceAsync_ShouldKeepOneAndDiscardOthers() throws Exception {
        // GIVEN
        var deviceRelationsQuery = new DeviceRelationsQuery();
        deviceRelationsQuery.setDeviceTypes(List.of("Device type 1", "Device type 2", "default"));
        deviceRelationsQuery.setDirection(EntitySearchDirection.FROM);
        deviceRelationsQuery.setMaxLevel(2);
        deviceRelationsQuery.setRelationType(EntityRelation.CONTAINS_TYPE);

        var expectedDeviceSearchQuery = new DeviceSearchQuery();
        var parameters = new RelationsSearchParameters(
                DUMMY_ORIGINATOR,
                deviceRelationsQuery.getDirection(),
                deviceRelationsQuery.getMaxLevel(),
                deviceRelationsQuery.isFetchLastLevelOnly()
        );
        expectedDeviceSearchQuery.setParameters(parameters);
        expectedDeviceSearchQuery.setRelationType(deviceRelationsQuery.getRelationType());
        expectedDeviceSearchQuery.setDeviceTypes(deviceRelationsQuery.getDeviceTypes());

        var device1 = new Device(new DeviceId(UUID.randomUUID()));
        device1.setName("Device 1");
        var device2 = new Device(new DeviceId(UUID.randomUUID()));
        device1.setName("Device 2");
        var device3 = new Device(new DeviceId(UUID.randomUUID()));
        device1.setName("Device 3");

        var devicesList = List.of(device1, device2, device3);

        when(ctxMock.getTenantId()).thenReturn(TENANT_ID);
        when(ctxMock.getDeviceService()).thenReturn(deviceServiceMock);
        when(deviceServiceMock.findDevicesByQuery(eq(TENANT_ID), eq(expectedDeviceSearchQuery)))
                .thenReturn(Futures.immediateFuture(devicesList));
        when(ctxMock.getDbCallbackExecutor()).thenReturn(DB_EXECUTOR);

        // WHEN
        var entityIdFuture = EntitiesRelatedDeviceIdAsyncLoader.findDeviceAsync(ctxMock, DUMMY_ORIGINATOR, deviceRelationsQuery);

        // THEN
        assertNotNull(entityIdFuture);

        var actualEntityId = entityIdFuture.get();
        assertNotNull(actualEntityId);
        assertEquals(device1.getId(), actualEntityId);
    }

}
