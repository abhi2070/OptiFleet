
package org.thingsboard.server.transport.mqtt.util;

import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReturnCodeResolver {

    public static MqttConnectReturnCode getConnectionReturnCode(MqttVersion mqttVersion, ReturnCode returnCode) {
        if (!MqttVersion.MQTT_5.equals(mqttVersion) && !ReturnCode.SUCCESS.equals(returnCode)) {
            switch (returnCode) {
                case BAD_USERNAME_OR_PASSWORD:
                case NOT_AUTHORIZED_5:
                    return MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED;
                case SERVER_UNAVAILABLE_5:
                    return MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE;
                case CLIENT_IDENTIFIER_NOT_VALID:
                    return MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED;
                default:
                    log.warn("Unknown return code for conversion: {}", returnCode.name());
            }
        }
        return MqttConnectReturnCode.valueOf(returnCode.byteValue());
    }

    public static int getSubscriptionReturnCode(MqttVersion mqttVersion, ReturnCode returnCode) {
        if (!MqttVersion.MQTT_5.equals(mqttVersion) && !ReturnCode.SUCCESS.equals(returnCode)) {
            switch (returnCode) {
                case UNSPECIFIED_ERROR:
                case TOPIC_FILTER_INVALID:
                case IMPLEMENTATION_SPECIFIC:
                case NOT_AUTHORIZED_5:
                case PACKET_IDENTIFIER_IN_USE:
                case QUOTA_EXCEEDED:
                case SHARED_SUBSCRIPTION_NOT_SUPPORTED:
                case SUBSCRIPTION_IDENTIFIERS_NOT_SUPPORTED:
                case WILDCARD_SUBSCRIPTION_NOT_SUPPORTED:
                    return MqttQoS.FAILURE.value();
            }
        }
        return returnCode.byteValue();
    }
}
