
package org.thingsboard.server.queue.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.ExecutorProvider;
import org.thingsboard.common.util.ThingsBoardExecutors;
import org.thingsboard.common.util.ThingsBoardThreadFactory;
import org.thingsboard.server.queue.TbQueueAdmin;
import org.thingsboard.server.queue.util.PropertyUtils;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class TbAwsSqsAdmin implements TbQueueAdmin {

    private final Map<String, String> attributes;
    private final AmazonSQS sqsClient;
    private final Map<String, String> queues;
    @Getter
    private final ExecutorService producerExecutor;

    public TbAwsSqsAdmin(TbAwsSqsSettings sqsSettings, Map<String, String> attributes) {
        this.attributes = attributes;

        AWSCredentialsProvider credentialsProvider;
        if (sqsSettings.getUseDefaultCredentialProviderChain()) {
            credentialsProvider = new DefaultAWSCredentialsProviderChain();
        } else {
            AWSCredentials awsCredentials = new BasicAWSCredentials(sqsSettings.getAccessKeyId(), sqsSettings.getSecretAccessKey());
            credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        }
        producerExecutor = ThingsBoardExecutors.newWorkStealingPool(sqsSettings.getThreadPoolSize(), "aws-sqs-queue-executor");

        sqsClient = AmazonSQSClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(sqsSettings.getRegion())
                .build();

        queues = sqsClient
                .listQueues()
                .getQueueUrls()
                .stream()
                .map(this::getQueueNameFromUrl)
                .collect(Collectors.toMap(this::convertTopicToQueueName, Function.identity()));
    }

    @Override
    public void createTopicIfNotExists(String topic, String properties) {
        String queueName = convertTopicToQueueName(topic);
        if (queues.containsKey(queueName)) {
            return;
        }
        Map<String, String> attributes = PropertyUtils.getProps(this.attributes, properties, TbAwsSqsQueueAttributes::toConfigs);
        final CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName).withAttributes(attributes);
        String queueUrl = sqsClient.createQueue(createQueueRequest).getQueueUrl();
        queues.put(getQueueNameFromUrl(queueUrl), queueUrl);
    }

    private String convertTopicToQueueName(String topic) {
        return topic.replaceAll("\\.", "_") + ".fifo";
    }

    @Override
    public void deleteTopic(String topic) {
        String queueName = convertTopicToQueueName(topic);
        if (queues.containsKey(queueName)) {
            sqsClient.deleteQueue(queues.get(queueName));
        } else {
            GetQueueUrlResult queueUrl = sqsClient.getQueueUrl(queueName);
            if (queueUrl != null) {
                sqsClient.deleteQueue(queueUrl.getQueueUrl());
            } else {
                log.warn("Aws SQS queue [{}] does not exist!", queueName);
            }
        }
    }

    private String getQueueNameFromUrl(String queueUrl) {
        int delimiterIndex = queueUrl.lastIndexOf("/");
        return queueUrl.substring(delimiterIndex + 1);
    }

    @Override
    public void destroy() {
        if (sqsClient != null) {
            sqsClient.shutdown();
        }
        if (producerExecutor != null) {
            producerExecutor.shutdownNow();
        }
    }
}
