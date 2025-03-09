
package org.thingsboard.server.service.edge.rpc.constructor.device;

import com.fasterxml.jackson.databind.JsonNode;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.gen.edge.v1.DeviceCredentialsUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DeviceProfileUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DeviceRpcCallMsg;
import org.thingsboard.server.gen.edge.v1.DeviceUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

import java.util.UUID;

public interface DeviceMsgConstructor extends MsgConstructor {

    DeviceUpdateMsg constructDeviceUpdatedMsg(UpdateMsgType msgType, Device device);

    DeviceUpdateMsg constructDeviceDeleteMsg(DeviceId deviceId);

    DeviceCredentialsUpdateMsg constructDeviceCredentialsUpdatedMsg(DeviceCredentials deviceCredentials);

    DeviceProfileUpdateMsg constructDeviceProfileUpdatedMsg(UpdateMsgType msgType, DeviceProfile deviceProfile);

    DeviceProfileUpdateMsg constructDeviceProfileDeleteMsg(DeviceProfileId deviceProfileId);

    DeviceRpcCallMsg constructDeviceRpcCallMsg(UUID deviceId, JsonNode body);
}
