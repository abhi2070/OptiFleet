
package org.thingsboard.server.service.edge.rpc.processor.device;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.gen.edge.v1.DeviceCredentialsUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DeviceUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Primary
@Component
@TbCoreComponent
public class DeviceEdgeProcessorV2 extends DeviceEdgeProcessor {

    @Override
    protected Device constructDeviceFromUpdateMsg(TenantId tenantId, DeviceId deviceId, DeviceUpdateMsg deviceUpdateMsg) {
        return JacksonUtil.fromString(deviceUpdateMsg.getEntity(), Device.class, true);
    }

    @Override
    protected DeviceCredentials constructDeviceCredentialsFromUpdateMsg(TenantId tenantId, DeviceCredentialsUpdateMsg deviceCredentialsUpdateMsg) {
        return JacksonUtil.fromString(deviceCredentialsUpdateMsg.getEntity(), DeviceCredentials.class, true);
    }

    @Override
    protected void setCustomerId(TenantId tenantId, CustomerId customerId, Device device, DeviceUpdateMsg deviceUpdateMsg) {
        CustomerId customerUUID = device.getCustomerId() != null ? device.getCustomerId() : customerId;
        device.setCustomerId(customerUUID);
    }
}
