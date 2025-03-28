
package org.thingsboard.server.queue.provider;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToVersionControlServiceMsg;
import org.thingsboard.server.queue.TbQueueAdmin;
import org.thingsboard.server.queue.TbQueueConsumer;
import org.thingsboard.server.queue.TbQueueProducer;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;
import org.thingsboard.server.queue.discovery.TbServiceInfoProvider;
import org.thingsboard.server.queue.discovery.TopicService;
import org.thingsboard.server.queue.kafka.TbKafkaAdmin;
import org.thingsboard.server.queue.kafka.TbKafkaConsumerStatsService;
import org.thingsboard.server.queue.kafka.TbKafkaConsumerTemplate;
import org.thingsboard.server.queue.kafka.TbKafkaProducerTemplate;
import org.thingsboard.server.queue.kafka.TbKafkaSettings;
import org.thingsboard.server.queue.kafka.TbKafkaTopicConfigs;
import org.thingsboard.server.queue.settings.TbQueueCoreSettings;
import org.thingsboard.server.queue.settings.TbQueueVersionControlSettings;

import javax.annotation.PreDestroy;

@Component
@ConditionalOnExpression("'${queue.type:null}'=='kafka' && '${service.type:null}'=='tb-vc-executor'")
public class KafkaTbVersionControlQueueFactory implements TbVersionControlQueueFactory {

    private final TbKafkaSettings kafkaSettings;
    private final TbServiceInfoProvider serviceInfoProvider;
    private final TbQueueCoreSettings coreSettings;
    private final TbQueueVersionControlSettings vcSettings;
    private final TbKafkaConsumerStatsService consumerStatsService;
    private final TopicService topicService;

    private final TbQueueAdmin coreAdmin;
    private final TbQueueAdmin vcAdmin;
    private final TbQueueAdmin notificationAdmin;

    public KafkaTbVersionControlQueueFactory(TbKafkaSettings kafkaSettings,
                                             TbServiceInfoProvider serviceInfoProvider,
                                             TbQueueCoreSettings coreSettings,
                                             TbQueueVersionControlSettings vcSettings,
                                             TbKafkaConsumerStatsService consumerStatsService,
                                             TbKafkaTopicConfigs kafkaTopicConfigs,
                                             TopicService topicService) {
        this.kafkaSettings = kafkaSettings;
        this.serviceInfoProvider = serviceInfoProvider;
        this.coreSettings = coreSettings;
        this.vcSettings = vcSettings;
        this.consumerStatsService = consumerStatsService;
        this.topicService = topicService;

        this.coreAdmin = new TbKafkaAdmin(kafkaSettings, kafkaTopicConfigs.getCoreConfigs());
        this.vcAdmin = new TbKafkaAdmin(kafkaSettings, kafkaTopicConfigs.getVcConfigs());
        this.notificationAdmin = new TbKafkaAdmin(kafkaSettings, kafkaTopicConfigs.getNotificationsConfigs());
    }


    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreNotificationMsg>> createTbCoreNotificationsMsgProducer() {
        TbKafkaProducerTemplate.TbKafkaProducerTemplateBuilder<TbProtoQueueMsg<ToCoreNotificationMsg>> requestBuilder = TbKafkaProducerTemplate.builder();
        requestBuilder.settings(kafkaSettings);
        requestBuilder.clientId("tb-vc-to-core-notifications-" + serviceInfoProvider.getServiceId());
        requestBuilder.defaultTopic(topicService.buildTopicName(coreSettings.getTopic()));
        requestBuilder.admin(notificationAdmin);
        return requestBuilder.build();
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToVersionControlServiceMsg>> createToVersionControlMsgConsumer() {
        TbKafkaConsumerTemplate.TbKafkaConsumerTemplateBuilder<TbProtoQueueMsg<ToVersionControlServiceMsg>> consumerBuilder = TbKafkaConsumerTemplate.builder();
        consumerBuilder.settings(kafkaSettings);
        consumerBuilder.topic(topicService.buildTopicName(vcSettings.getTopic()));
        consumerBuilder.clientId("tb-vc-consumer-" + serviceInfoProvider.getServiceId());
        consumerBuilder.groupId(topicService.buildTopicName("tb-vc-node"));
        consumerBuilder.decoder(msg -> new TbProtoQueueMsg<>(msg.getKey(), ToVersionControlServiceMsg.parseFrom(msg.getData()), msg.getHeaders()));
        consumerBuilder.admin(vcAdmin);
        consumerBuilder.statsService(consumerStatsService);
        return consumerBuilder.build();
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgProducer() {
        TbKafkaProducerTemplate.TbKafkaProducerTemplateBuilder<TbProtoQueueMsg<ToUsageStatsServiceMsg>> requestBuilder = TbKafkaProducerTemplate.builder();
        requestBuilder.settings(kafkaSettings);
        requestBuilder.clientId("tb-vc-us-producer-" + serviceInfoProvider.getServiceId());
        requestBuilder.defaultTopic(topicService.buildTopicName(coreSettings.getUsageStatsTopic()));
        requestBuilder.admin(coreAdmin);
        return requestBuilder.build();
    }

    @PreDestroy
    private void destroy() {
        if (coreAdmin != null) {
            coreAdmin.destroy();
        }
        if (vcAdmin != null) {
            vcAdmin.destroy();
        }
        if (notificationAdmin != null) {
            notificationAdmin.destroy();
        }
    }
}
