
package org.thingsboard.server.queue.provider;

import com.google.protobuf.util.JsonFormat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.queue.Queue;
import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.gen.js.JsInvokeProtos;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToRuleEngineNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToTransportMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import org.thingsboard.server.queue.TbQueueAdmin;
import org.thingsboard.server.queue.TbQueueConsumer;
import org.thingsboard.server.queue.TbQueueProducer;
import org.thingsboard.server.queue.TbQueueRequestTemplate;
import org.thingsboard.server.queue.common.DefaultTbQueueRequestTemplate;
import org.thingsboard.server.queue.common.TbProtoJsQueueMsg;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;
import org.thingsboard.server.queue.discovery.TopicService;
import org.thingsboard.server.queue.discovery.TbServiceInfoProvider;
import org.thingsboard.server.queue.pubsub.TbPubSubAdmin;
import org.thingsboard.server.queue.pubsub.TbPubSubConsumerTemplate;
import org.thingsboard.server.queue.pubsub.TbPubSubProducerTemplate;
import org.thingsboard.server.queue.pubsub.TbPubSubSettings;
import org.thingsboard.server.queue.pubsub.TbPubSubSubscriptionSettings;
import org.thingsboard.server.queue.settings.TbQueueCoreSettings;
import org.thingsboard.server.queue.settings.TbQueueRemoteJsInvokeSettings;
import org.thingsboard.server.queue.settings.TbQueueRuleEngineSettings;
import org.thingsboard.server.queue.settings.TbQueueTransportNotificationSettings;

import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;

@Component
@ConditionalOnExpression("'${queue.type:null}'=='pubsub' && '${service.type:null}'=='tb-rule-engine'")
public class PubSubTbRuleEngineQueueFactory implements TbRuleEngineQueueFactory {

    private final TbPubSubSettings pubSubSettings;
    private final TbQueueCoreSettings coreSettings;
    private final TbQueueRuleEngineSettings ruleEngineSettings;
    private final TopicService topicService;
    private final TbServiceInfoProvider serviceInfoProvider;
    private final TbQueueRemoteJsInvokeSettings jsInvokeSettings;
    private final TbQueueTransportNotificationSettings transportNotificationSettings;

    private final TbQueueAdmin coreAdmin;
    private final TbQueueAdmin ruleEngineAdmin;
    private final TbQueueAdmin jsExecutorAdmin;
    private final TbQueueAdmin notificationAdmin;

    public PubSubTbRuleEngineQueueFactory(TbPubSubSettings pubSubSettings,
                                          TbQueueCoreSettings coreSettings,
                                          TbQueueRuleEngineSettings ruleEngineSettings,
                                          TopicService topicService,
                                          TbServiceInfoProvider serviceInfoProvider,
                                          TbQueueRemoteJsInvokeSettings jsInvokeSettings,
                                          TbQueueTransportNotificationSettings transportNotificationSettings,
                                          TbPubSubSubscriptionSettings pubSubSubscriptionSettings) {
        this.pubSubSettings = pubSubSettings;
        this.coreSettings = coreSettings;
        this.ruleEngineSettings = ruleEngineSettings;
        this.topicService = topicService;
        this.serviceInfoProvider = serviceInfoProvider;
        this.jsInvokeSettings = jsInvokeSettings;
        this.transportNotificationSettings = transportNotificationSettings;

        this.coreAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getCoreSettings());
        this.ruleEngineAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getRuleEngineSettings());
        this.jsExecutorAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getJsExecutorSettings());
        this.notificationAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getNotificationsSettings());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToTransportMsg>> createTransportNotificationsMsgProducer() {
        return new TbPubSubProducerTemplate<>(notificationAdmin, pubSubSettings, topicService.buildTopicName(transportNotificationSettings.getNotificationsTopic()));
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToRuleEngineMsg>> createRuleEngineMsgProducer() {
        return new TbPubSubProducerTemplate<>(ruleEngineAdmin, pubSubSettings, topicService.buildTopicName(ruleEngineSettings.getTopic()));
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToRuleEngineNotificationMsg>> createRuleEngineNotificationsMsgProducer() {
        return new TbPubSubProducerTemplate<>(notificationAdmin, pubSubSettings, topicService.buildTopicName(ruleEngineSettings.getTopic()));
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreMsg>> createTbCoreMsgProducer() {
        return new TbPubSubProducerTemplate<>(coreAdmin, pubSubSettings, topicService.buildTopicName(coreSettings.getTopic()));
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreNotificationMsg>> createTbCoreNotificationsMsgProducer() {
        return new TbPubSubProducerTemplate<>(notificationAdmin, pubSubSettings, topicService.buildTopicName(coreSettings.getTopic()));
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToRuleEngineMsg>> createToRuleEngineMsgConsumer(Queue configuration) {
        return new TbPubSubConsumerTemplate<>(ruleEngineAdmin, pubSubSettings, topicService.buildTopicName(configuration.getTopic()),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), ToRuleEngineMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToRuleEngineNotificationMsg>> createToRuleEngineNotificationsMsgConsumer() {
        return new TbPubSubConsumerTemplate<>(notificationAdmin, pubSubSettings,
                topicService.getNotificationsTopic(ServiceType.TB_RULE_ENGINE, serviceInfoProvider.getServiceId()).getFullTopicName(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), ToRuleEngineNotificationMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    @Bean
    public TbQueueRequestTemplate<TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>, TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> createRemoteJsRequestTemplate() {
        TbQueueProducer<TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>> producer = new TbPubSubProducerTemplate<>(jsExecutorAdmin, pubSubSettings, jsInvokeSettings.getRequestTopic());
        TbQueueConsumer<TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> consumer = new TbPubSubConsumerTemplate<>(jsExecutorAdmin, pubSubSettings,
                jsInvokeSettings.getResponseTopic() + "." + serviceInfoProvider.getServiceId(),
                msg -> {
                    JsInvokeProtos.RemoteJsResponse.Builder builder = JsInvokeProtos.RemoteJsResponse.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(new String(msg.getData(), StandardCharsets.UTF_8), builder);
                    return new TbProtoQueueMsg<>(msg.getKey(), builder.build(), msg.getHeaders());
                });

        DefaultTbQueueRequestTemplate.DefaultTbQueueRequestTemplateBuilder
                <TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>, TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> builder = DefaultTbQueueRequestTemplate.builder();
        builder.queueAdmin(jsExecutorAdmin);
        builder.requestTemplate(producer);
        builder.responseTemplate(consumer);
        builder.maxPendingRequests(jsInvokeSettings.getMaxPendingRequests());
        builder.maxRequestTimeout(jsInvokeSettings.getMaxRequestsTimeout());
        builder.pollInterval(jsInvokeSettings.getResponsePollInterval());
        return builder.build();
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgProducer() {
        return new TbPubSubProducerTemplate<>(coreAdmin, pubSubSettings, topicService.buildTopicName(coreSettings.getUsageStatsTopic()));
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgProducer() {
        return new TbPubSubProducerTemplate<>(coreAdmin, pubSubSettings, topicService.buildTopicName(coreSettings.getOtaPackageTopic()));
    }

    @PreDestroy
    private void destroy() {
        if (coreAdmin != null) {
            coreAdmin.destroy();
        }
        if (ruleEngineAdmin != null) {
            ruleEngineAdmin.destroy();
        }
        if (jsExecutorAdmin != null) {
            jsExecutorAdmin.destroy();
        }
        if (notificationAdmin != null) {
            notificationAdmin.destroy();
        }
    }
}
