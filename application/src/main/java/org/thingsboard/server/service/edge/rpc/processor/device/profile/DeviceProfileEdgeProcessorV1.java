
package org.thingsboard.server.service.edge.rpc.processor.device.profile;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.DeviceProfileProvisionType;
import org.thingsboard.server.common.data.DeviceProfileType;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.device.profile.DeviceProfileData;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.DeviceProfileUpdateMsg;
import org.thingsboard.server.queue.util.DataDecodingEncodingService;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
@TbCoreComponent
public class DeviceProfileEdgeProcessorV1 extends DeviceProfileEdgeProcessor {

    @Autowired
    private DataDecodingEncodingService dataDecodingEncodingService;

    @Override
    protected DeviceProfile constructDeviceProfileFromUpdateMsg(TenantId tenantId, DeviceProfileId deviceProfileId, DeviceProfileUpdateMsg deviceProfileUpdateMsg) {
        DeviceProfile deviceProfile = new DeviceProfile();
        deviceProfile.setTenantId(tenantId);
        deviceProfile.setCreatedTime(Uuids.unixTimestamp(deviceProfileId.getId()));
        deviceProfile.setName(deviceProfileUpdateMsg.getName());
        deviceProfile.setDefault(deviceProfileUpdateMsg.getDefault());
        deviceProfile.setDescription(deviceProfileUpdateMsg.hasDescription() ? deviceProfileUpdateMsg.getDescription() : null);
        deviceProfile.setType(DeviceProfileType.valueOf(deviceProfileUpdateMsg.getType()));
        deviceProfile.setTransportType(deviceProfileUpdateMsg.hasTransportType()
                ? DeviceTransportType.valueOf(deviceProfileUpdateMsg.getTransportType()) : DeviceTransportType.DEFAULT);
        deviceProfile.setImage(deviceProfileUpdateMsg.hasImage()
                ? new String(deviceProfileUpdateMsg.getImage().toByteArray(), StandardCharsets.UTF_8) : null);
        deviceProfile.setProvisionType(deviceProfileUpdateMsg.hasProvisionType()
                ? DeviceProfileProvisionType.valueOf(deviceProfileUpdateMsg.getProvisionType()) : DeviceProfileProvisionType.DISABLED);
        deviceProfile.setProvisionDeviceKey(deviceProfileUpdateMsg.hasProvisionDeviceKey()
                ? deviceProfileUpdateMsg.getProvisionDeviceKey() : null);
        deviceProfile.setDefaultQueueName(deviceProfileUpdateMsg.getDefaultQueueName());

        Optional<DeviceProfileData> profileDataOpt =
                dataDecodingEncodingService.decode(deviceProfileUpdateMsg.getProfileDataBytes().toByteArray());
        deviceProfile.setProfileData(profileDataOpt.orElse(null));

        String defaultQueueName = StringUtils.isNotBlank(deviceProfileUpdateMsg.getDefaultQueueName())
                ? deviceProfileUpdateMsg.getDefaultQueueName() : null;
        deviceProfile.setDefaultQueueName(defaultQueueName);

        UUID firmwareUUID = safeGetUUID(deviceProfileUpdateMsg.getFirmwareIdMSB(), deviceProfileUpdateMsg.getFirmwareIdLSB());
        deviceProfile.setFirmwareId(firmwareUUID != null ? new OtaPackageId(firmwareUUID) : null);

        UUID softwareUUID = safeGetUUID(deviceProfileUpdateMsg.getSoftwareIdMSB(), deviceProfileUpdateMsg.getSoftwareIdLSB());
        deviceProfile.setSoftwareId(softwareUUID != null ? new OtaPackageId(softwareUUID) : null);
        return deviceProfile;
    }

    @Override
    protected void setDefaultRuleChainId(TenantId tenantId, DeviceProfile deviceProfile, RuleChainId ruleChainId) {
        deviceProfile.setDefaultRuleChainId(ruleChainId);
    }

    @Override
    protected void setDefaultEdgeRuleChainId(DeviceProfile deviceProfile, RuleChainId ruleChainId, DeviceProfileUpdateMsg deviceProfileUpdateMsg) {
        UUID defaultEdgeRuleChainUUID = safeGetUUID(deviceProfileUpdateMsg.getDefaultRuleChainIdMSB(), deviceProfileUpdateMsg.getDefaultRuleChainIdLSB());
        deviceProfile.setDefaultEdgeRuleChainId(defaultEdgeRuleChainUUID != null ? new RuleChainId(defaultEdgeRuleChainUUID) : ruleChainId);
    }

    @Override
    protected void setDefaultDashboardId(TenantId tenantId, DashboardId dashboardId, DeviceProfile deviceProfile, DeviceProfileUpdateMsg deviceProfileUpdateMsg) {
        UUID defaultDashboardUUID = safeGetUUID(deviceProfileUpdateMsg.getDefaultDashboardIdMSB(), deviceProfileUpdateMsg.getDefaultDashboardIdLSB());
        deviceProfile.setDefaultDashboardId(defaultDashboardUUID != null ? new DashboardId(defaultDashboardUUID) : dashboardId);
    }
}
