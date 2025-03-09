
package org.thingsboard.server.service.queue.ruleengine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.TbQueueConsumer;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;

import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class TbQueueConsumerTask {

    @Getter
    private final Object key;
    @Getter
    private final TbQueueConsumer<TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> consumer;

    @Setter
    private Future<?> task;

    public void subscribe(Set<TopicPartitionInfo> partitions) {
        log.trace("[{}] Subscribing to partitions: {}", key, partitions);
        consumer.subscribe(partitions);
    }

    public void initiateStop() {
        log.debug("[{}] Initiating stop", key);
        consumer.stop();
    }

    public void awaitCompletion() {
        log.trace("[{}] Awaiting finish", key);
        if (isRunning()) {
            try {
                task.get(30, TimeUnit.SECONDS);
                log.trace("[{}] Awaited finish", key);
            } catch (Exception e) {
                log.warn("[{}] Failed to await for consumer to stop", key, e);
            }
            task = null;
        }
    }

    public boolean isRunning() {
        return task != null;
    }

}
