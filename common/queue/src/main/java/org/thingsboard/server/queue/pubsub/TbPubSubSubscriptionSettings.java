
package org.thingsboard.server.queue.pubsub;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.PropertyUtils;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
@ConditionalOnExpression("'${queue.type:null}'=='pubsub'")
public class TbPubSubSubscriptionSettings {
    @Value("${queue.pubsub.queue-properties.core:}")
    private String coreProperties;
    @Value("${queue.pubsub.queue-properties.rule-engine:}")
    private String ruleEngineProperties;
    @Value("${queue.pubsub.queue-properties.transport-api:}")
    private String transportApiProperties;
    @Value("${queue.pubsub.queue-properties.notifications:}")
    private String notificationsProperties;
    @Value("${queue.pubsub.queue-properties.js-executor:}")
    private String jsExecutorProperties;
    @Value("${queue.pubsub.queue-properties.version-control:}")
    private String vcProperties;

    @Getter
    private Map<String, String> coreSettings;
    @Getter
    private Map<String, String> ruleEngineSettings;
    @Getter
    private Map<String, String> transportApiSettings;
    @Getter
    private Map<String, String> notificationsSettings;
    @Getter
    private Map<String, String> jsExecutorSettings;
    @Getter
    private Map<String, String> vcSettings;

    @PostConstruct
    private void init() {
        coreSettings = PropertyUtils.getProps(coreProperties);
        ruleEngineSettings = PropertyUtils.getProps(ruleEngineProperties);
        transportApiSettings = PropertyUtils.getProps(transportApiProperties);
        notificationsSettings = PropertyUtils.getProps(notificationsProperties);
        jsExecutorSettings = PropertyUtils.getProps(jsExecutorProperties);
        vcSettings = PropertyUtils.getProps(vcProperties);
    }

}
