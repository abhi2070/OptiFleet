
package org.thingsboard.server.service.edge.rpc.constructor.device;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.gen.edge.v1.DeviceCredentialsUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DeviceProfileUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DeviceUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class DeviceMsgConstructorV2 extends BaseDeviceMsgConstructor {

    @Override
    public DeviceUpdateMsg constructDeviceUpdatedMsg(UpdateMsgType msgType, Device device) {
        return DeviceUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(device))
                .setIdMSB(device.getId().getId().getMostSignificantBits())
                .setIdLSB(device.getId().getId().getLeastSignificantBits()).build();
    }

    @Override
    public DeviceCredentialsUpdateMsg constructDeviceCredentialsUpdatedMsg(DeviceCredentials deviceCredentials) {
        return DeviceCredentialsUpdateMsg.newBuilder().setEntity(JacksonUtil.toString(deviceCredentials)).build();
    }

    @Override
    public DeviceProfileUpdateMsg constructDeviceProfileUpdatedMsg(UpdateMsgType msgType, DeviceProfile deviceProfile) {
        return DeviceProfileUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(deviceProfile))
                .setIdMSB(deviceProfile.getId().getId().getMostSignificantBits())
                .setIdLSB(deviceProfile.getId().getId().getLeastSignificantBits()).build();
    }
}
