
package org.thingsboard.server.service.queue.ruleengine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.thingsboard.server.common.data.queue.Queue;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;

import java.util.Set;

@Getter
@ToString
@AllArgsConstructor
public class TbQueueConsumerManagerTask {

    private final QueueEvent event;
    private Queue queue;
    private Set<TopicPartitionInfo> partitions;
    private boolean drainQueue;

    public static TbQueueConsumerManagerTask delete(boolean drainQueue) {
        return new TbQueueConsumerManagerTask(QueueEvent.DELETE, null, null, drainQueue);
    }

    public static TbQueueConsumerManagerTask configUpdate(Queue queue) {
        return new TbQueueConsumerManagerTask(QueueEvent.CONFIG_UPDATE, queue, null, false);
    }

    public static TbQueueConsumerManagerTask partitionChange(Set<TopicPartitionInfo> partitions) {
        return new TbQueueConsumerManagerTask(QueueEvent.PARTITION_CHANGE, null, partitions, false);
    }

}
