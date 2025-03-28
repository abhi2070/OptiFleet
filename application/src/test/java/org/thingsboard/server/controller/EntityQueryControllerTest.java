
package org.thingsboard.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.query.AlarmCountQuery;
import org.thingsboard.server.common.data.query.DeviceTypeFilter;
import org.thingsboard.server.common.data.query.DynamicValue;
import org.thingsboard.server.common.data.query.DynamicValueSourceType;
import org.thingsboard.server.common.data.query.EntityCountQuery;
import org.thingsboard.server.common.data.query.EntityData;
import org.thingsboard.server.common.data.query.EntityDataPageLink;
import org.thingsboard.server.common.data.query.EntityDataQuery;
import org.thingsboard.server.common.data.query.EntityDataSortOrder;
import org.thingsboard.server.common.data.query.EntityKey;
import org.thingsboard.server.common.data.query.EntityKeyType;
import org.thingsboard.server.common.data.query.EntityListFilter;
import org.thingsboard.server.common.data.query.EntityTypeFilter;
import org.thingsboard.server.common.data.query.FilterPredicateValue;
import org.thingsboard.server.common.data.query.KeyFilter;
import org.thingsboard.server.common.data.query.NumericFilterPredicate;
import org.thingsboard.server.common.data.query.TsValue;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.service.DaoSqlTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DaoSqlTest
public class EntityQueryControllerTest extends AbstractControllerTest {

    private Tenant savedTenant;
    private User tenantAdmin;

    @Before
    public void beforeTest() throws Exception {
        loginSysAdmin();

        Tenant tenant = new Tenant();
        tenant.setTitle("My tenant");
        savedTenant = doPost("/api/tenant", tenant, Tenant.class);
        Assert.assertNotNull(savedTenant);

        tenantAdmin = new User();
        tenantAdmin.setAuthority(Authority.TENANT_ADMIN);
        tenantAdmin.setTenantId(savedTenant.getId());
        tenantAdmin.setEmail("tenant2@thingsboard.org");
        tenantAdmin.setFirstName("Joe");
        tenantAdmin.setLastName("Downs");

        tenantAdmin = createUserAndLogin(tenantAdmin, "testPassword1");
    }

    @After
    public void afterTest() throws Exception {
        loginSysAdmin();

        doDelete("/api/tenant/" + savedTenant.getId().getId().toString())
                .andExpect(status().isOk());
    }

    @Test
    public void testTenantCountEntitiesByQuery() throws Exception {
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 97; i++) {
            Device device = new Device();
            device.setName("Device" + i);
            device.setType("default");
            device.setLabel("testLabel" + (int) (Math.random() * 1000));
            devices.add(doPost("/api/device", device, Device.class));
            Thread.sleep(1);
        }
        DeviceTypeFilter filter = new DeviceTypeFilter();
        filter.setDeviceTypes(List.of("default"));
        filter.setDeviceNameFilter("");

        EntityCountQuery countQuery = new EntityCountQuery(filter);

        Long count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());

        filter.setDeviceTypes(List.of("unknown"));
        count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(0, count.longValue());

        filter.setDeviceTypes(List.of("default"));
        filter.setDeviceNameFilter("Device1");

        count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(11, count.longValue());

        EntityListFilter entityListFilter = new EntityListFilter();
        entityListFilter.setEntityType(EntityType.DEVICE);
        entityListFilter.setEntityList(devices.stream().map(Device::getId).map(DeviceId::toString).collect(Collectors.toList()));

        countQuery = new EntityCountQuery(entityListFilter);

        count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());

        EntityTypeFilter filter2 = new EntityTypeFilter();
        filter2.setEntityType(EntityType.DEVICE);

        EntityCountQuery countQuery2 = new EntityCountQuery(filter2);

        Long count2 = doPostWithResponse("/api/entitiesQuery/count", countQuery2, Long.class);
        Assert.assertEquals(97, count2.longValue());
    }

    @Test
    public void testSysAdminCountEntitiesByQuery() throws Exception {
        loginSysAdmin();

        EntityTypeFilter allDeviceFilter = new EntityTypeFilter();
        allDeviceFilter.setEntityType(EntityType.DEVICE);
        EntityCountQuery query = new EntityCountQuery(allDeviceFilter);
        Long initialCount = doPostWithResponse("/api/entitiesQuery/count", query, Long.class);

        loginTenantAdmin();

        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 97; i++) {
            Device device = new Device();
            device.setName("Device" + i);
            device.setType("default");
            device.setLabel("testLabel" + (int) (Math.random() * 1000));
            devices.add(doPost("/api/device", device, Device.class));
            Thread.sleep(1);
        }
        DeviceTypeFilter filter = new DeviceTypeFilter();
        filter.setDeviceType("default");
        filter.setDeviceNameFilter("");

        loginSysAdmin();

        EntityCountQuery countQuery = new EntityCountQuery(filter);

        Long count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());

        filter.setDeviceType("unknown");
        count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(0, count.longValue());

        filter.setDeviceType("default");
        filter.setDeviceNameFilter("Device1");

        count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(11, count.longValue());

        EntityListFilter entityListFilter = new EntityListFilter();
        entityListFilter.setEntityType(EntityType.DEVICE);
        entityListFilter.setEntityList(devices.stream().map(Device::getId).map(DeviceId::toString).collect(Collectors.toList()));

        countQuery = new EntityCountQuery(entityListFilter);

        count = doPostWithResponse("/api/entitiesQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());

        Long count2 = doPostWithResponse("/api/entitiesQuery/count", query, Long.class);
        Assert.assertEquals(initialCount + 97, count2.longValue());
    }

    @Test
    public void testTenantCountAlarmsByQuery() throws Exception {
        loginTenantAdmin();
        List<Device> devices = new ArrayList<>();
        List<Alarm> alarms = new ArrayList<>();
        for (int i = 0; i < 97; i++) {
            Device device = new Device();
            device.setName("Device" + i);
            device.setType("default");
            device.setLabel("testLabel" + (int) (Math.random() * 1000));
            devices.add(doPost("/api/device", device, Device.class));
            Thread.sleep(1);
        }

        for (int i = 0; i < devices.size(); i++) {
            Alarm alarm = new Alarm();
            alarm.setOriginator(devices.get(i).getId());
            alarm.setType("alarm" + i);
            alarm.setSeverity(AlarmSeverity.WARNING);
            alarms.add(doPost("/api/alarm", alarm, Alarm.class));
            Thread.sleep(1);
        }
        testCountAlarmsByQuery(alarms);
    }

    @Test
    public void testCustomerCountAlarmsByQuery() throws Exception {
        loginTenantAdmin();
        List<Device> devices = new ArrayList<>();
        List<Alarm> alarms = new ArrayList<>();
        for (int i = 0; i < 97; i++) {
            Device device = new Device();
            device.setCustomerId(customerId);
            device.setName("Device" + i);
            device.setType("default");
            device.setLabel("testLabel" + (int) (Math.random() * 1000));
            devices.add(doPost("/api/device", device, Device.class));
            Thread.sleep(1);
        }

        loginCustomerUser();

        for (int i = 0; i < devices.size(); i++) {
            Alarm alarm = new Alarm();
            alarm.setCustomerId(customerId);
            alarm.setOriginator(devices.get(i).getId());
            alarm.setType("alarm" + i);
            alarm.setSeverity(AlarmSeverity.WARNING);
            alarms.add(doPost("/api/alarm", alarm, Alarm.class));
            Thread.sleep(1);
        }
        testCountAlarmsByQuery(alarms);
    }

    private void testCountAlarmsByQuery(List<Alarm> alarms) throws Exception {
        AlarmCountQuery countQuery = new AlarmCountQuery();

        Long count = doPostWithResponse("/api/alarmsQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());

        countQuery = AlarmCountQuery.builder()
                .typeList(List.of("unknown"))
                .build();

        count = doPostWithResponse("/api/alarmsQuery/count", countQuery, Long.class);
        Assert.assertEquals(0, count.longValue());

        countQuery = AlarmCountQuery.builder()
                .typeList(List.of("alarm1", "alarm2", "alarm3"))
                .build();

        count = doPostWithResponse("/api/alarmsQuery/count", countQuery, Long.class);
        Assert.assertEquals(3, count.longValue());

        countQuery = AlarmCountQuery.builder()
                .typeList(alarms.stream().map(Alarm::getType).collect(Collectors.toList()))
                .build();

        count = doPostWithResponse("/api/alarmsQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());

        countQuery = AlarmCountQuery.builder()
                .severityList(List.of(AlarmSeverity.CRITICAL))
                .build();

        count = doPostWithResponse("/api/alarmsQuery/count", countQuery, Long.class);
        Assert.assertEquals(0, count.longValue());

        countQuery = AlarmCountQuery.builder()
                .severityList(List.of(AlarmSeverity.WARNING))
                .build();

        count = doPostWithResponse("/api/alarmsQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());

        long startTs = alarms.stream().map(Alarm::getCreatedTime).min(Long::compareTo).get();
        long endTs = alarms.stream().map(Alarm::getCreatedTime).max(Long::compareTo).get();

        countQuery = AlarmCountQuery.builder()
                .startTs(startTs - 1)
                .endTs(endTs + 1)
                .build();

        count = doPostWithResponse("/api/alarmsQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());

        countQuery = AlarmCountQuery.builder()
                .startTs(0)
                .endTs(endTs + 1)
                .build();

        count = doPostWithResponse("/api/alarmsQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());

        countQuery = AlarmCountQuery.builder()
                .startTs(0)
                .endTs(System.currentTimeMillis())
                .build();

        count = doPostWithResponse("/api/alarmsQuery/count", countQuery, Long.class);
        Assert.assertEquals(97, count.longValue());

        countQuery = AlarmCountQuery.builder()
                .startTs(endTs + 1)
                .endTs(System.currentTimeMillis())
                .build();

        count = doPostWithResponse("/api/alarmsQuery/count", countQuery, Long.class);
        Assert.assertEquals(0, count.longValue());
    }

    @Test
    public void testSimpleFindEntityDataByQuery() throws Exception {
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 97; i++) {
            Device device = new Device();
            device.setName("Device" + i);
            device.setType("default");
            device.setLabel("testLabel" + (int) (Math.random() * 1000));
            devices.add(doPost("/api/device", device, Device.class));
            Thread.sleep(1);
        }

        DeviceTypeFilter filter = new DeviceTypeFilter();
        filter.setDeviceTypes(List.of("default"));
        filter.setDeviceNameFilter("");

        EntityDataSortOrder sortOrder = new EntityDataSortOrder(
                new EntityKey(EntityKeyType.ENTITY_FIELD, "createdTime"), EntityDataSortOrder.Direction.ASC
        );
        EntityDataPageLink pageLink = new EntityDataPageLink(10, 0, null, sortOrder);
        List<EntityKey> entityFields = Collections.singletonList(new EntityKey(EntityKeyType.ENTITY_FIELD, "name"));

        EntityDataQuery query = new EntityDataQuery(filter, pageLink, entityFields, null, null);

        PageData<EntityData> data =
                doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
                });

        Assert.assertEquals(97, data.getTotalElements());
        Assert.assertEquals(10, data.getTotalPages());
        Assert.assertTrue(data.hasNext());
        Assert.assertEquals(10, data.getData().size());

        List<EntityData> loadedEntities = new ArrayList<>(data.getData());
        while (data.hasNext()) {
            query = query.next();
            data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
            });
            loadedEntities.addAll(data.getData());
        }
        Assert.assertEquals(97, loadedEntities.size());

        List<EntityId> loadedIds = loadedEntities.stream().map(EntityData::getEntityId).collect(Collectors.toList());
        List<EntityId> deviceIds = devices.stream().map(Device::getId).collect(Collectors.toList());

        Assert.assertEquals(deviceIds, loadedIds);

        List<String> loadedNames = loadedEntities.stream().map(entityData ->
                entityData.getLatest().get(EntityKeyType.ENTITY_FIELD).get("name").getValue()).collect(Collectors.toList());
        List<String> deviceNames = devices.stream().map(Device::getName).collect(Collectors.toList());

        Assert.assertEquals(deviceNames, loadedNames);

        sortOrder = new EntityDataSortOrder(
                new EntityKey(EntityKeyType.ENTITY_FIELD, "name"), EntityDataSortOrder.Direction.DESC
        );

        pageLink = new EntityDataPageLink(10, 0, "device1", sortOrder);
        query = new EntityDataQuery(filter, pageLink, entityFields, null, null);
        data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
        });
        Assert.assertEquals(11, data.getTotalElements());
        Assert.assertEquals("Device19", data.getData().get(0).getLatest().get(EntityKeyType.ENTITY_FIELD).get("name").getValue());


        EntityTypeFilter filter2 = new EntityTypeFilter();
        filter2.setEntityType(EntityType.DEVICE);

        EntityDataSortOrder sortOrder2 = new EntityDataSortOrder(
                new EntityKey(EntityKeyType.ENTITY_FIELD, "createdTime"), EntityDataSortOrder.Direction.ASC
        );
        EntityDataPageLink pageLink2 = new EntityDataPageLink(10, 0, null, sortOrder2);
        List<EntityKey> entityFields2 = Collections.singletonList(new EntityKey(EntityKeyType.ENTITY_FIELD, "name"));

        EntityDataQuery query2 = new EntityDataQuery(filter2, pageLink2, entityFields2, null, null);

        PageData<EntityData> data2 =
                doPostWithTypedResponse("/api/entitiesQuery/find", query2, new TypeReference<PageData<EntityData>>() {
                });

        Assert.assertEquals(97, data2.getTotalElements());
        Assert.assertEquals(10, data2.getTotalPages());
        Assert.assertTrue(data2.hasNext());
        Assert.assertEquals(10, data2.getData().size());

    }

    @Test
    public void testFindEntityDataByQueryWithAttributes() throws Exception {
        List<Device> devices = new ArrayList<>();
        List<Long> temperatures = new ArrayList<>();
        List<Long> highTemperatures = new ArrayList<>();
        for (int i = 0; i < 67; i++) {
            Device device = new Device();
            String name = "Device" + i;
            device.setName(name);
            device.setType("default");
            device.setLabel("testLabel" + (int) (Math.random() * 1000));
            devices.add(doPost("/api/device?accessToken=" + name, device, Device.class));
            Thread.sleep(1);
            long temperature = (long) (Math.random() * 100);
            temperatures.add(temperature);
            if (temperature > 45) {
                highTemperatures.add(temperature);
            }
        }
        for (int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            String payload = "{\"temperature\":" + temperatures.get(i) + "}";
            doPost("/api/plugins/telemetry/" + device.getId() + "/" + DataConstants.SHARED_SCOPE, payload, String.class, status().isOk());
        }
        Thread.sleep(1000);

        DeviceTypeFilter filter = new DeviceTypeFilter();
        filter.setDeviceTypes(List.of("default"));
        filter.setDeviceNameFilter("");

        EntityDataSortOrder sortOrder = new EntityDataSortOrder(
                new EntityKey(EntityKeyType.ENTITY_FIELD, "createdTime"), EntityDataSortOrder.Direction.ASC
        );
        EntityDataPageLink pageLink = new EntityDataPageLink(10, 0, null, sortOrder);
        List<EntityKey> entityFields = Collections.singletonList(new EntityKey(EntityKeyType.ENTITY_FIELD, "name"));
        List<EntityKey> latestValues = Collections.singletonList(new EntityKey(EntityKeyType.ATTRIBUTE, "temperature"));

        EntityDataQuery query = new EntityDataQuery(filter, pageLink, entityFields, latestValues, null);
        PageData<EntityData> data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
        });

        List<EntityData> loadedEntities = new ArrayList<>(data.getData());
        while (data.hasNext()) {
            query = query.next();
            data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
            });
            loadedEntities.addAll(data.getData());
        }
        Assert.assertEquals(67, loadedEntities.size());

        List<String> loadedTemperatures = loadedEntities.stream().map(entityData ->
                entityData.getLatest().get(EntityKeyType.ATTRIBUTE).get("temperature").getValue()).collect(Collectors.toList());
        List<String> deviceTemperatures = temperatures.stream().map(aLong -> Long.toString(aLong)).collect(Collectors.toList());
        Assert.assertEquals(deviceTemperatures, loadedTemperatures);

        pageLink = new EntityDataPageLink(10, 0, null, sortOrder);
        KeyFilter highTemperatureFilter = new KeyFilter();
        highTemperatureFilter.setKey(new EntityKey(EntityKeyType.ATTRIBUTE, "temperature"));
        NumericFilterPredicate predicate = new NumericFilterPredicate();
        predicate.setValue(FilterPredicateValue.fromDouble(45));
        predicate.setOperation(NumericFilterPredicate.NumericOperation.GREATER);
        highTemperatureFilter.setPredicate(predicate);
        List<KeyFilter> keyFilters = Collections.singletonList(highTemperatureFilter);

        query = new EntityDataQuery(filter, pageLink, entityFields, latestValues, keyFilters);

        data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
        });
        loadedEntities = new ArrayList<>(data.getData());
        while (data.hasNext()) {
            query = query.next();
            data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {
            });
            loadedEntities.addAll(data.getData());
        }
        Assert.assertEquals(highTemperatures.size(), loadedEntities.size());

        List<String> loadedHighTemperatures = loadedEntities.stream().map(entityData ->
                entityData.getLatest().get(EntityKeyType.ATTRIBUTE).get("temperature").getValue()).collect(Collectors.toList());
        List<String> deviceHighTemperatures = highTemperatures.stream().map(aLong -> Long.toString(aLong)).collect(Collectors.toList());

        Assert.assertEquals(deviceHighTemperatures, loadedHighTemperatures);
    }

    @Test
    public void testFindEntityDataByQueryWithDynamicValue() throws Exception {
        int numOfDevices = 2;

        for (int i = 0; i < numOfDevices; i++) {
            Device device = new Device();
            String name = "Device" + i;
            device.setName(name);
            device.setType("default");
            device.setLabel("testLabel" + (int) (Math.random() * 1000));

            Device savedDevice1 = doPost("/api/device?accessToken=" + name, device, Device.class);
            JsonNode content = JacksonUtil.toJsonNode("{\"alarmActiveTime\": 1" + i + "}");
            doPost("/api/plugins/telemetry/" + EntityType.DEVICE.name() + "/" + savedDevice1.getUuidId() + "/SERVER_SCOPE", content)
                    .andExpect(status().isOk());
        }
        JsonNode content = JacksonUtil.toJsonNode("{\"dynamicValue\": 0}");
        doPost("/api/plugins/telemetry/" + EntityType.TENANT.name() + "/" + tenantId.getId() + "/SERVER_SCOPE", content)
                .andExpect(status().isOk());


        DeviceTypeFilter filter = new DeviceTypeFilter();
        filter.setDeviceTypes(List.of("default"));
        filter.setDeviceNameFilter("");

        KeyFilter highTemperatureFilter = new KeyFilter();
        highTemperatureFilter.setKey(new EntityKey(EntityKeyType.SERVER_ATTRIBUTE, "alarmActiveTime"));
        NumericFilterPredicate predicate = new NumericFilterPredicate();

        DynamicValue<Double> dynamicValue =
                new DynamicValue<>(DynamicValueSourceType.CURRENT_TENANT, "dynamicValue");
        FilterPredicateValue<Double> predicateValue = new FilterPredicateValue<>(0.0, null, dynamicValue);

        predicate.setValue(predicateValue);
        predicate.setOperation(NumericFilterPredicate.NumericOperation.GREATER);
        highTemperatureFilter.setPredicate(predicate);

        List<KeyFilter> keyFilters = Collections.singletonList(highTemperatureFilter);


        EntityDataSortOrder sortOrder = new EntityDataSortOrder(
                new EntityKey(EntityKeyType.ENTITY_FIELD, "createdTime"), EntityDataSortOrder.Direction.ASC
        );
        EntityDataPageLink pageLink = new EntityDataPageLink(10, 0, null, sortOrder);

        List<EntityKey> entityFields = Collections.singletonList(new EntityKey(EntityKeyType.ENTITY_FIELD, "name"));
        List<EntityKey> latestValues = Collections.singletonList(new EntityKey(EntityKeyType.ATTRIBUTE, "alarmActiveTime"));

        EntityDataQuery query = new EntityDataQuery(filter, pageLink, entityFields, latestValues, keyFilters);

        Awaitility.await()
                .alias("data by query")
                .atMost(30, TimeUnit.SECONDS)
                .until(() -> {
                    var data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {});
                    var loadedEntities = new ArrayList<>(data.getData());
                    return loadedEntities.size() == numOfDevices;
                });

        var data = doPostWithTypedResponse("/api/entitiesQuery/find", query, new TypeReference<PageData<EntityData>>() {});
        var loadedEntities = new ArrayList<>(data.getData());

        Assert.assertEquals(numOfDevices, loadedEntities.size());

        for (int i = 0; i < numOfDevices; i++) {
            var entity = loadedEntities.get(i);
            String name = entity.getLatest().get(EntityKeyType.ENTITY_FIELD).getOrDefault("name", new TsValue(0, "Invalid")).getValue();
            String alarmActiveTime = entity.getLatest().get(EntityKeyType.ATTRIBUTE).getOrDefault("alarmActiveTime", new TsValue(0, "-1")).getValue();

            Assert.assertEquals("Device" + i, name);
            Assert.assertEquals("1" + i, alarmActiveTime);
        }
    }

    @Test
    public void givenInvalidEntityDataPageLink_thenReturnError() throws Exception {
        DeviceTypeFilter filter = new DeviceTypeFilter();
        filter.setDeviceTypes(List.of("default"));
        filter.setDeviceNameFilter("");

        String invalidSortProperty = "created(Time)";
        EntityDataSortOrder sortOrder = new EntityDataSortOrder(
                new EntityKey(EntityKeyType.ENTITY_FIELD, invalidSortProperty), EntityDataSortOrder.Direction.ASC
        );
        EntityDataPageLink pageLink = new EntityDataPageLink(10, 0, null, sortOrder);
        List<EntityKey> entityFields = Collections.singletonList(new EntityKey(EntityKeyType.ENTITY_FIELD, "name"));
        List<EntityKey> latestValues = Collections.singletonList(new EntityKey(EntityKeyType.ATTRIBUTE, "temperature"));
        EntityDataQuery query = new EntityDataQuery(filter, pageLink, entityFields, latestValues, null);

        ResultActions result = doPost("/api/entitiesQuery/find", query).andExpect(status().isBadRequest());
        assertThat(getErrorMessage(result)).contains("Invalid").contains("sort property");
    }

}
