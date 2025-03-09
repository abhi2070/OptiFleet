
package org.thingsboard.server.queue.notification;

import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.notification.rule.trigger.NotificationRuleTrigger;
import org.thingsboard.server.common.msg.notification.NotificationRuleProcessor;
import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;
import org.thingsboard.server.queue.discovery.TopicService;
import org.thingsboard.server.queue.discovery.PartitionService;
import org.thingsboard.server.queue.provider.TbQueueProducerProvider;
import org.thingsboard.server.queue.util.DataDecodingEncodingService;

import java.util.UUID;

@Service
@ConditionalOnMissingBean(value = NotificationRuleProcessor.class, ignored = RemoteNotificationRuleProcessor.class)
@RequiredArgsConstructor
@Slf4j
public class RemoteNotificationRuleProcessor implements NotificationRuleProcessor {

    private final NotificationDeduplicationService deduplicationService;
    private final TbQueueProducerProvider producerProvider;
    private final TopicService topicService;
    private final PartitionService partitionService;
    private final DataDecodingEncodingService encodingService;

    @Override
    public void process(NotificationRuleTrigger trigger) {
        try {
            if (trigger.deduplicate() && deduplicationService.alreadyProcessed(trigger)) {
                return;
            }

            log.debug("Submitting notification rule trigger: {}", trigger);
            TransportProtos.NotificationRuleProcessorMsg.Builder msg = TransportProtos.NotificationRuleProcessorMsg.newBuilder()
                    .setTrigger(ByteString.copyFrom(encodingService.encode(trigger)));

            partitionService.getAllServiceIds(ServiceType.TB_CORE).stream().findAny().ifPresent(serviceId -> {
                TopicPartitionInfo tpi = topicService.getNotificationsTopic(ServiceType.TB_CORE, serviceId);
                producerProvider.getTbCoreNotificationsMsgProducer().send(tpi, new TbProtoQueueMsg<>(UUID.randomUUID(),
                        TransportProtos.ToCoreNotificationMsg.newBuilder()
                                .setNotificationRuleProcessorMsg(msg)
                                .build()), null);
            });
        } catch (Throwable e) {
            log.error("Failed to submit notification rule trigger: {}", trigger, e);
        }
    }

}
