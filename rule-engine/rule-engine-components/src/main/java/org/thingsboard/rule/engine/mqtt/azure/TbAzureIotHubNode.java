
package org.thingsboard.rule.engine.mqtt.azure;

import io.netty.handler.codec.mqtt.MqttVersion;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.AzureIotHubUtil;
import org.thingsboard.mqtt.MqttClientConfig;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.rule.engine.credentials.CertPemCredentials;
import org.thingsboard.rule.engine.credentials.ClientCredentials;
import org.thingsboard.rule.engine.credentials.CredentialsType;
import org.thingsboard.rule.engine.mqtt.TbMqttNode;
import org.thingsboard.rule.engine.mqtt.TbMqttNodeConfiguration;
import org.thingsboard.server.common.data.plugin.ComponentClusteringMode;
import org.thingsboard.server.common.data.plugin.ComponentType;

import javax.net.ssl.SSLException;

@Slf4j
@RuleNode(
        type = ComponentType.EXTERNAL,
        name = "azure iot hub",
        configClazz = TbAzureIotHubNodeConfiguration.class,
        clusteringMode = ComponentClusteringMode.SINGLETON,
        nodeDescription = "Publish messages to the Azure IoT Hub",
        nodeDetails = "Will publish message payload to the Azure IoT Hub with QoS <b>AT_LEAST_ONCE</b>.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbExternalNodeAzureIotHubConfig"
)
public class TbAzureIotHubNode extends TbMqttNode {
    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        super.init(ctx);
        this.mqttNodeConfiguration = TbNodeUtils.convert(configuration, TbMqttNodeConfiguration.class);
        try {
            mqttNodeConfiguration.setPort(8883);
            mqttNodeConfiguration.setCleanSession(true);
            ClientCredentials credentials = mqttNodeConfiguration.getCredentials();
            if (CredentialsType.CERT_PEM == credentials.getType()) {
                CertPemCredentials pemCredentials = (CertPemCredentials) credentials;
                if (pemCredentials.getCaCert() == null || pemCredentials.getCaCert().isEmpty()) {
                    pemCredentials.setCaCert(AzureIotHubUtil.getDefaultCaCert());
                }
            }
            this.mqttClient = initClient(ctx);
        } catch (Exception e) {
            throw new TbNodeException(e);
        }
    }

    protected void prepareMqttClientConfig(MqttClientConfig config) throws SSLException {
        config.setProtocolVersion(MqttVersion.MQTT_3_1_1);
        config.setUsername(AzureIotHubUtil.buildUsername(mqttNodeConfiguration.getHost(), config.getClientId()));
        ClientCredentials credentials = mqttNodeConfiguration.getCredentials();
        if (CredentialsType.SAS == credentials.getType()) {
            config.setPassword(AzureIotHubUtil.buildSasToken(mqttNodeConfiguration.getHost(), ((AzureIotHubSasCredentials) credentials).getSasKey()));
        }
    }
}
