
package org.thingsboard.server.dao.service;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.DeviceProfileType;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.OtaPackage;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.device.profile.DefaultDeviceProfileConfiguration;
import org.thingsboard.server.common.data.device.profile.DefaultDeviceProfileTransportConfiguration;
import org.thingsboard.server.common.data.device.profile.DeviceProfileData;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.event.RuleNodeDebugEvent;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.dao.audit.AuditLogLevelFilter;
import org.thingsboard.server.dao.audit.AuditLogLevelMask;
import org.thingsboard.server.dao.audit.AuditLogLevelProperties;
import org.thingsboard.server.dao.tenant.TenantService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AbstractServiceTest.class, loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Configuration
@ComponentScan("org.thingsboard.server")
public abstract class AbstractServiceTest {

    public static final TenantId SYSTEM_TENANT_ID = TenantId.SYS_TENANT_ID;

    @Autowired
    protected TenantService tenantService;

    protected TenantId tenantId;

    @Before
    public void beforeAbstractService() {
        tenantId = createTenant().getId();
    }

    @After
    public void afterAbstractService() {
        tenantService.deleteTenants();
    }

    public class IdComparator<D extends HasId> implements Comparator<D> {
        @Override
        public int compare(D o1, D o2) {
            return o1.getId().getId().compareTo(o2.getId().getId());
        }
    }


    protected RuleNodeDebugEvent generateEvent(TenantId tenantId, EntityId entityId) throws IOException {
        if (tenantId == null) {
            tenantId = TenantId.fromUUID(Uuids.timeBased());
        }
        return RuleNodeDebugEvent.builder()
                .tenantId(tenantId)
                .entityId(entityId.getId())
                .serviceId("server A")
                .data(JacksonUtil.toString(readFromResource("TestJsonData.json")))
                .build();
    }

    public JsonNode readFromResource(String resourceName) throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourceName)){
            return JacksonUtil.fromBytes(Objects.requireNonNull(is).readAllBytes());
        }
    }

    @Bean
    public AuditLogLevelFilter auditLogLevelFilter() {
        Map<String, String> mask = new HashMap<>();
        for (EntityType entityType : EntityType.values()) {
            mask.put(entityType.name().toLowerCase(), AuditLogLevelMask.RW.name());
        }
        var props = new AuditLogLevelProperties();
        props.setMask(mask);
        return new AuditLogLevelFilter(props);
    }

    protected DeviceProfile createDeviceProfile(TenantId tenantId, String name) {
        DeviceProfile deviceProfile = new DeviceProfile();
        deviceProfile.setTenantId(tenantId);
        deviceProfile.setName(name);
        deviceProfile.setType(DeviceProfileType.DEFAULT);
        deviceProfile.setTransportType(DeviceTransportType.DEFAULT);
        deviceProfile.setDescription(name + " Test");
        DeviceProfileData deviceProfileData = new DeviceProfileData();
        DefaultDeviceProfileConfiguration configuration = new DefaultDeviceProfileConfiguration();
        DefaultDeviceProfileTransportConfiguration transportConfiguration = new DefaultDeviceProfileTransportConfiguration();
        deviceProfileData.setConfiguration(configuration);
        deviceProfileData.setTransportConfiguration(transportConfiguration);
        deviceProfile.setProfileData(deviceProfileData);
        deviceProfile.setDefault(false);
        deviceProfile.setDefaultRuleChainId(null);
        return deviceProfile;
    }

    protected AssetProfile createAssetProfile(TenantId tenantId, String name) {
        AssetProfile assetProfile = new AssetProfile();
        assetProfile.setTenantId(tenantId);
        assetProfile.setName(name);
        assetProfile.setDescription(name + " Test");
        assetProfile.setDefault(false);
        assetProfile.setDefaultRuleChainId(null);
        return assetProfile;
    }

    public Tenant createTenant() {
        Tenant tenant = new Tenant();
        tenant.setTitle("My tenant " + UUID.randomUUID());
        Tenant savedTenant = tenantService.saveTenant(tenant);
        assertNotNull(savedTenant);
        return savedTenant;
    }

    protected Edge constructEdge(TenantId tenantId, String name, String type) {
        Edge edge = new Edge();
        edge.setTenantId(tenantId);
        edge.setName(name);
        edge.setType(type);
        edge.setSecret(StringUtils.randomAlphanumeric(20));
        edge.setRoutingKey(StringUtils.randomAlphanumeric(20));
        return edge;
    }

    protected OtaPackage constructDefaultOtaPackage(TenantId tenantId, DeviceProfileId deviceProfileId) {
        OtaPackage firmware = new OtaPackage();
        firmware.setTenantId(tenantId);
        firmware.setDeviceProfileId(deviceProfileId);
        firmware.setType(OtaPackageType.FIRMWARE);
        firmware.setTitle("My firmware");
        firmware.setVersion("3.3.3");
        firmware.setFileName("filename.txt");
        firmware.setContentType("text/plain");
        firmware.setChecksumAlgorithm(ChecksumAlgorithm.SHA256);
        firmware.setChecksum("4bf5122f344554c53bde2ebb8cd2b7e3d1600ad631c385a5d7cce23c7785459a");
        firmware.setData(ByteBuffer.wrap(new byte[]{1}));
        firmware.setDataSize(1L);
        return firmware;
    }

}
