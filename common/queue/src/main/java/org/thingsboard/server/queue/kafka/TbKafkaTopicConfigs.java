
package org.thingsboard.server.queue.kafka;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.PropertyUtils;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "queue", value = "type", havingValue = "kafka")
public class TbKafkaTopicConfigs {
    public static final String NUM_PARTITIONS_SETTING = "partitions";

    @Value("${queue.kafka.topic-properties.core:}")
    private String coreProperties;
    @Value("${queue.kafka.topic-properties.rule-engine:}")
    private String ruleEngineProperties;
    @Value("${queue.kafka.topic-properties.transport-api:}")
    private String transportApiProperties;
    @Value("${queue.kafka.topic-properties.notifications:}")
    private String notificationsProperties;
    @Value("${queue.kafka.topic-properties.js-executor:}")
    private String jsExecutorProperties;
    @Value("${queue.kafka.topic-properties.ota-updates:}")
    private String fwUpdatesProperties;
    @Value("${queue.kafka.topic-properties.version-control:}")
    private String vcProperties;

    @Getter
    private Map<String, String> coreConfigs;
    @Getter
    private Map<String, String> ruleEngineConfigs;
    @Getter
    private Map<String, String> transportApiRequestConfigs;
    @Getter
    private Map<String, String> transportApiResponseConfigs;
    @Getter
    private Map<String, String> notificationsConfigs;
    @Getter
    private Map<String, String> jsExecutorRequestConfigs;
    @Getter
    private Map<String, String> jsExecutorResponseConfigs;
    @Getter
    private Map<String, String> fwUpdatesConfigs;
    @Getter
    private Map<String, String> vcConfigs;

    @PostConstruct
    private void init() {
        coreConfigs = PropertyUtils.getProps(coreProperties);
        ruleEngineConfigs = PropertyUtils.getProps(ruleEngineProperties);
        transportApiRequestConfigs = PropertyUtils.getProps(transportApiProperties);
        transportApiResponseConfigs = PropertyUtils.getProps(transportApiProperties);
        transportApiResponseConfigs.put(NUM_PARTITIONS_SETTING, "1");
        notificationsConfigs = PropertyUtils.getProps(notificationsProperties);
        jsExecutorRequestConfigs = PropertyUtils.getProps(jsExecutorProperties);
        jsExecutorResponseConfigs = PropertyUtils.getProps(jsExecutorProperties);
        jsExecutorResponseConfigs.put(NUM_PARTITIONS_SETTING, "1");
        fwUpdatesConfigs = PropertyUtils.getProps(fwUpdatesProperties);
        vcConfigs = PropertyUtils.getProps(vcProperties);
    }

}
