
package org.thingsboard.server.queue.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.thingsboard.server.queue.TbQueueAdmin;
import org.thingsboard.server.queue.util.PropertyUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by ashvayka on 24.09.18.
 */
@Slf4j
public class TbKafkaAdmin implements TbQueueAdmin {

    private final AdminClient client;
    private final Map<String, String> topicConfigs;
    private final Set<String> topics = ConcurrentHashMap.newKeySet();
    private final int numPartitions;

    private final short replicationFactor;

    public TbKafkaAdmin(TbKafkaSettings settings, Map<String, String> topicConfigs) {
        client = AdminClient.create(settings.toAdminProps());
        this.topicConfigs = topicConfigs;

        try {
            topics.addAll(client.listTopics().names().get());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get all topics.", e);
        }

        String numPartitionsStr = topicConfigs.get(TbKafkaTopicConfigs.NUM_PARTITIONS_SETTING);
        if (numPartitionsStr != null) {
            numPartitions = Integer.parseInt(numPartitionsStr);
            topicConfigs.remove("partitions");
        } else {
            numPartitions = 1;
        }
        replicationFactor = settings.getReplicationFactor();
    }

    @Override
    public void createTopicIfNotExists(String topic, String properties) {
        if (topics.contains(topic)) {
            return;
        }
        try {
            NewTopic newTopic = new NewTopic(topic, numPartitions, replicationFactor).configs(PropertyUtils.getProps(topicConfigs, properties));
            createTopic(newTopic).values().get(topic).get();
            topics.add(topic);
        } catch (ExecutionException ee) {
            if (ee.getCause() instanceof TopicExistsException) {
                //do nothing
            } else {
                log.warn("[{}] Failed to create topic", topic, ee);
                throw new RuntimeException(ee);
            }
        } catch (Exception e) {
            log.warn("[{}] Failed to create topic", topic, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTopic(String topic) {
        if (topics.contains(topic)) {
            client.deleteTopics(Collections.singletonList(topic));
        } else {
            try {
                if (client.listTopics().names().get().contains(topic)) {
                    client.deleteTopics(Collections.singletonList(topic));
                } else {
                    log.warn("Kafka topic [{}] does not exist.", topic);
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to delete kafka topic [{}].", topic, e);
            }
        }
    }

    @Override
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }

    public CreateTopicsResult createTopic(NewTopic topic) {
        return client.createTopics(Collections.singletonList(topic));
    }
}
