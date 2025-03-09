
package org.thingsboard.server.queue.discovery.event;

import lombok.Getter;
import lombok.ToString;
import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.common.msg.queue.TopicPartitionInfo;
import org.thingsboard.server.queue.discovery.QueueKey;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@ToString(callSuper = true)
public class PartitionChangeEvent extends TbApplicationEvent {

    private static final long serialVersionUID = -8731788167026510559L;

    @Getter
    private final ServiceType serviceType;
    @Getter
    private final Map<QueueKey, Set<TopicPartitionInfo>> partitionsMap;

    public PartitionChangeEvent(Object source, ServiceType serviceType, Map<QueueKey, Set<TopicPartitionInfo>> partitionsMap) {
        super(source);
        this.serviceType = serviceType;
        this.partitionsMap = partitionsMap;
    }

    // only for service types that have single QueueKey
    public Set<TopicPartitionInfo> getPartitions() {
        return partitionsMap.values().stream().findAny().orElse(Collections.emptySet());
    }

}
