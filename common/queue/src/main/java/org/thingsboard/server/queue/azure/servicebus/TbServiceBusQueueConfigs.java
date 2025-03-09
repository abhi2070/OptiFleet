
package org.thingsboard.server.queue.azure.servicebus;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.PropertyUtils;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
@ConditionalOnExpression("'${queue.type:null}'=='service-bus'")
public class TbServiceBusQueueConfigs {
    @Value("${queue.service-bus.queue-properties.core:}")
    private String coreProperties;
    @Value("${queue.service-bus.queue-properties.rule-engine:}")
    private String ruleEngineProperties;
    @Value("${queue.service-bus.queue-properties.transport-api:}")
    private String transportApiProperties;
    @Value("${queue.service-bus.queue-properties.notifications:}")
    private String notificationsProperties;
    @Value("${queue.service-bus.queue-properties.js-executor:}")
    private String jsExecutorProperties;
    @Value("${queue.service-bus.queue-properties.version-control:}")
    private String vcProperties;
    @Getter
    private Map<String, String> coreConfigs;
    @Getter
    private Map<String, String> ruleEngineConfigs;
    @Getter
    private Map<String, String> transportApiConfigs;
    @Getter
    private Map<String, String> notificationsConfigs;
    @Getter
    private Map<String, String> jsExecutorConfigs;
    @Getter
    private Map<String, String> vcConfigs;

    @PostConstruct
    private void init() {
        coreConfigs = PropertyUtils.getProps(coreProperties);
        ruleEngineConfigs = PropertyUtils.getProps(ruleEngineProperties);
        transportApiConfigs = PropertyUtils.getProps(transportApiProperties);
        notificationsConfigs = PropertyUtils.getProps(notificationsProperties);
        jsExecutorConfigs = PropertyUtils.getProps(jsExecutorProperties);
        vcConfigs = PropertyUtils.getProps(vcProperties);
    }

}
