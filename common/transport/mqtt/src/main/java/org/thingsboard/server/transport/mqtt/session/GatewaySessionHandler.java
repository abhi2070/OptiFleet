
package org.thingsboard.server.transport.mqtt.session;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import org.thingsboard.server.common.adaptor.AdaptorException;
import org.thingsboard.server.common.transport.auth.GetOrCreateDeviceFromGatewayResponse;

import java.util.UUID;

/**
 * Created by nickAS21 on 26.12.22
 */
public class GatewaySessionHandler extends AbstractGatewaySessionHandler {

    public GatewaySessionHandler(DeviceSessionCtx deviceSessionCtx, UUID sessionId) {
        super(deviceSessionCtx, sessionId);
    }

    public void onDeviceConnect(MqttPublishMessage mqttMsg) throws AdaptorException {
        if (isJsonPayloadType()) {
            onDeviceConnectJson(mqttMsg);
        } else {
            onDeviceConnectProto(mqttMsg);
        }
    }

    public void onDeviceTelemetry(MqttPublishMessage mqttMsg) throws AdaptorException {
        int msgId = getMsgId(mqttMsg);
        ByteBuf payload = mqttMsg.payload();
        if (isJsonPayloadType()) {
            onDeviceTelemetryJson(msgId, payload);
        } else {
            onDeviceTelemetryProto(msgId, payload);
        }
    }

    @Override
    protected GatewayDeviceSessionContext newDeviceSessionCtx(GetOrCreateDeviceFromGatewayResponse msg) {
         return new GatewayDeviceSessionContext(this, msg.getDeviceInfo(), msg.getDeviceProfile(), mqttQoSMap, transportService);
    }

}
