
package org.thingsboard.server.service.edge.rpc.processor.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.gen.edge.v1.DeviceCredentialsUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DeviceUpdateMsg;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;

@Slf4j
public abstract class BaseDeviceProcessor extends BaseEdgeProcessor {

    protected Pair<Boolean, Boolean> saveOrUpdateDevice(TenantId tenantId, DeviceId deviceId, DeviceUpdateMsg deviceUpdateMsg) {
        boolean created = false;
        boolean deviceNameUpdated = false;
        deviceCreationLock.lock();
        try {
            Device device = constructDeviceFromUpdateMsg(tenantId, deviceId, deviceUpdateMsg);
            if (device == null) {
                throw new RuntimeException("[{" + tenantId + "}] deviceUpdateMsg {" + deviceUpdateMsg + "} cannot be converted to device");
            }
            Device deviceById = deviceService.findDeviceById(tenantId, deviceId);
            if (deviceById == null) {
                created = true;
                device.setId(null);
            } else {
                device.setId(deviceId);
            }
            String deviceName = device.getName();
            Device deviceByName = deviceService.findDeviceByTenantIdAndName(tenantId, deviceName);
            if (deviceByName != null && !deviceByName.getId().equals(deviceId)) {
                deviceName = deviceName + "_" + StringUtils.randomAlphabetic(15);
                log.warn("[{}] Device with name {} already exists. Renaming device name to {}",
                        tenantId, device.getName(), deviceName);
                deviceNameUpdated = true;
            }
            device.setName(deviceName);
            setCustomerId(tenantId, created ? null : deviceById.getCustomerId(), device, deviceUpdateMsg);

            deviceValidator.validate(device, Device::getTenantId);
            if (created) {
                device.setId(deviceId);
            }
            Device savedDevice = deviceService.saveDevice(device, false);
            tbClusterService.onDeviceUpdated(savedDevice, created ? null : device);
        } catch (Exception e) {
            log.error("[{}] Failed to process device update msg [{}]", tenantId, deviceUpdateMsg, e);
            throw e;
        } finally {
            deviceCreationLock.unlock();
        }
        return Pair.of(created, deviceNameUpdated);
    }

    protected void updateDeviceCredentials(TenantId tenantId, DeviceCredentialsUpdateMsg deviceCredentialsUpdateMsg) {
        DeviceCredentials deviceCredentials = constructDeviceCredentialsFromUpdateMsg(tenantId, deviceCredentialsUpdateMsg);
        if (deviceCredentials == null) {
            throw new RuntimeException("[{" + tenantId + "}] deviceCredentialsUpdateMsg {" + deviceCredentialsUpdateMsg + "} cannot be converted to device credentials");
        }
        Device device = deviceService.findDeviceById(tenantId, deviceCredentials.getDeviceId());
        if (device != null) {
            log.debug("[{}] Updating device credentials for device [{}]. New device credentials Id [{}], value [{}]",
                    tenantId, device.getName(), deviceCredentials.getCredentialsId(), deviceCredentials.getCredentialsValue());
            try {
                DeviceCredentials deviceCredentialsByDeviceId = deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, device.getId());
                if (deviceCredentialsByDeviceId == null) {
                    deviceCredentialsByDeviceId = new DeviceCredentials();
                    deviceCredentialsByDeviceId.setDeviceId(device.getId());
                }
                deviceCredentialsByDeviceId.setCredentialsType(deviceCredentials.getCredentialsType());
                deviceCredentialsByDeviceId.setCredentialsId(deviceCredentials.getCredentialsId());
                deviceCredentialsByDeviceId.setCredentialsValue(deviceCredentials.getCredentialsValue());
                deviceCredentialsService.updateDeviceCredentials(tenantId, deviceCredentialsByDeviceId);

            } catch (Exception e) {
                log.error("[{}] Can't update device credentials for device [{}], deviceCredentialsUpdateMsg [{}]",
                        tenantId, device.getName(), deviceCredentialsUpdateMsg, e);
                throw new RuntimeException(e);
            }
        } else {
            log.warn("[{}] Can't find device by id [{}], deviceCredentialsUpdateMsg [{}]", tenantId, deviceCredentials.getDeviceId(), deviceCredentialsUpdateMsg);
        }
    }

    protected abstract Device constructDeviceFromUpdateMsg(TenantId tenantId, DeviceId deviceId, DeviceUpdateMsg deviceUpdateMsg);

    protected abstract void setCustomerId(TenantId tenantId, CustomerId customerId, Device device, DeviceUpdateMsg deviceUpdateMsg);

    protected abstract DeviceCredentials constructDeviceCredentialsFromUpdateMsg(TenantId tenantId, DeviceCredentialsUpdateMsg deviceCredentialsUpdateMsg);

}
