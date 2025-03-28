
package org.thingsboard.server.queue.provider;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.TbQueueAdmin;
import org.thingsboard.server.queue.TbQueueConsumer;
import org.thingsboard.server.queue.TbQueueProducer;
import org.thingsboard.server.queue.azure.servicebus.TbServiceBusAdmin;
import org.thingsboard.server.queue.azure.servicebus.TbServiceBusConsumerTemplate;
import org.thingsboard.server.queue.azure.servicebus.TbServiceBusProducerTemplate;
import org.thingsboard.server.queue.azure.servicebus.TbServiceBusQueueConfigs;
import org.thingsboard.server.queue.azure.servicebus.TbServiceBusSettings;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;
import org.thingsboard.server.queue.discovery.TopicService;
import org.thingsboard.server.queue.settings.TbQueueCoreSettings;
import org.thingsboard.server.queue.settings.TbQueueVersionControlSettings;

import javax.annotation.PreDestroy;

@Component
@ConditionalOnExpression("'${queue.type:null}'=='service-bus' && '${service.type:null}'=='tb-vc-executor'")
public class ServiceBusTbVersionControlQueueFactory implements TbVersionControlQueueFactory {

    private final TbServiceBusSettings serviceBusSettings;
    private final TbQueueCoreSettings coreSettings;
    private final TbQueueVersionControlSettings vcSettings;
    private final TopicService topicService;

    private final TbQueueAdmin coreAdmin;
    private final TbQueueAdmin notificationAdmin;
    private final TbQueueAdmin vcAdmin;

    public ServiceBusTbVersionControlQueueFactory(TbServiceBusSettings serviceBusSettings,
                                                  TbQueueCoreSettings coreSettings,
                                                  TbQueueVersionControlSettings vcSettings,
                                                  TbServiceBusQueueConfigs serviceBusQueueConfigs,
                                                  TopicService topicService
    ) {
        this.serviceBusSettings = serviceBusSettings;
        this.coreSettings = coreSettings;
        this.vcSettings = vcSettings;
        this.topicService = topicService;

        this.coreAdmin = new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getCoreConfigs());
        this.notificationAdmin = new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getNotificationsConfigs());
        this.vcAdmin = new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getVcConfigs());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgProducer() {
        return new TbServiceBusProducerTemplate<>(coreAdmin, serviceBusSettings, topicService.buildTopicName(coreSettings.getUsageStatsTopic()));
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToCoreNotificationMsg>> createTbCoreNotificationsMsgProducer() {
        return new TbServiceBusProducerTemplate<>(notificationAdmin, serviceBusSettings, topicService.buildTopicName(coreSettings.getTopic()));
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportProtos.ToVersionControlServiceMsg>> createToVersionControlMsgConsumer() {
        return new TbServiceBusConsumerTemplate<>(vcAdmin, serviceBusSettings, topicService.buildTopicName(vcSettings.getTopic()),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), TransportProtos.ToVersionControlServiceMsg.parseFrom(msg.getData()), msg.getHeaders())
        );
    }

    @PreDestroy
    private void destroy() {
        if (coreAdmin != null) {
            coreAdmin.destroy();
        }
        if (notificationAdmin != null) {
            notificationAdmin.destroy();
        }
        if (vcAdmin != null) {
            vcAdmin.destroy();
        }
    }
}
