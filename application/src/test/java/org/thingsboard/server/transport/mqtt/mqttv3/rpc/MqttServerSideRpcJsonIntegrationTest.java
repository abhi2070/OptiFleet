
package org.thingsboard.server.transport.mqtt.mqttv3.rpc;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.thingsboard.server.common.data.TransportPayloadType;
import org.thingsboard.server.dao.service.DaoSqlTest;
import org.thingsboard.server.transport.mqtt.MqttTestConfigProperties;
import org.thingsboard.server.transport.mqtt.mqttv3.MqttTestClient;

import static org.thingsboard.server.common.data.device.profile.MqttTopics.DEVICE_RPC_REQUESTS_SUB_SHORT_JSON_TOPIC;
import static org.thingsboard.server.common.data.device.profile.MqttTopics.DEVICE_RPC_REQUESTS_SUB_SHORT_TOPIC;
import static org.thingsboard.server.common.data.device.profile.MqttTopics.DEVICE_RPC_REQUESTS_SUB_TOPIC;

@Slf4j
@DaoSqlTest
public class MqttServerSideRpcJsonIntegrationTest extends AbstractMqttServerSideRpcIntegrationTest {

    @Before
    public void beforeTest() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("RPC test device")
                .gatewayName("RPC test gateway")
                .transportPayloadType(TransportPayloadType.JSON)
                .build();
        processBeforeTest(configProperties);
    }

    @Test
    public void testServerMqttOneWayRpc() throws Exception {
        processOneWayRpcTest(DEVICE_RPC_REQUESTS_SUB_TOPIC);
    }

    @Test
    public void testServerMqttOneWayRpcOnShortTopic() throws Exception {
        processOneWayRpcTest(DEVICE_RPC_REQUESTS_SUB_SHORT_TOPIC);
    }

    @Test
    public void testServerMqttOneWayRpcOnShortJsonTopic() throws Exception {
        processOneWayRpcTest(DEVICE_RPC_REQUESTS_SUB_SHORT_JSON_TOPIC);
    }

    @Test
    public void testServerMqttTwoWayRpc() throws Exception {
        processJsonTwoWayRpcTest(DEVICE_RPC_REQUESTS_SUB_TOPIC);
    }

    @Test
    public void testServerMqttTwoWayRpcOnShortTopic() throws Exception {
        processJsonTwoWayRpcTest(DEVICE_RPC_REQUESTS_SUB_SHORT_TOPIC);
    }

    @Test
    public void testServerMqttTwoWayRpcOnShortJsonTopic() throws Exception {
        processJsonTwoWayRpcTest(DEVICE_RPC_REQUESTS_SUB_SHORT_JSON_TOPIC);
    }

    @Test
    public void testGatewayServerMqttOneWayRpc() throws Exception {
        processJsonOneWayRpcTestGateway("Gateway Device OneWay RPC Json");
    }

    @Test
    public void testGatewayServerMqttTwoWayRpc() throws Exception {
        processJsonTwoWayRpcTestGateway("Gateway Device TwoWay RPC Json");
    }

    protected void processJsonOneWayRpcTestGateway(String deviceName) throws Exception {
        MqttTestClient client = new MqttTestClient();
        client.connectAndWait(gatewayAccessToken);
        String payload = "{\"device\": \"" + deviceName + "\", \"type\": \"" + TransportPayloadType.JSON.name() + "\"}";
        byte[] payloadBytes = payload.getBytes();
        validateOneWayRpcGatewayResponse(deviceName, client, payloadBytes);
        client.disconnect();
    }

}
