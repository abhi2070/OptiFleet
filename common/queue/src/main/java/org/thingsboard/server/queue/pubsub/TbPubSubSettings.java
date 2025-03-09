
package org.thingsboard.server.queue.pubsub;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.ThingsBoardThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.Executors;

@Slf4j
@ConditionalOnExpression("'${queue.type:null}'=='pubsub'")
@Component
@Data
public class TbPubSubSettings {

    @Value("${queue.pubsub.project_id}")
    private String projectId;

    @Value("${queue.pubsub.service_account}")
    private String serviceAccount;

    @Value("${queue.pubsub.max_msg_size}")
    private int maxMsgSize;

    @Value("${queue.pubsub.max_messages}")
    private int maxMessages;

    @Value("${queue.pubsub.executor_thread_pool_size:0}")
    private int threadPoolSize;

    /**
     * Refers to com.google.cloud.pubsub.v1.Publisher default executor configuration
     */
    private static final int THREADS_PER_CPU = 5;

    private FixedExecutorProvider executorProvider;

    private CredentialsProvider credentialsProvider;

    @PostConstruct
    private void init() throws IOException {
        ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(
                new ByteArrayInputStream(serviceAccount.getBytes()));
        credentialsProvider = FixedCredentialsProvider.create(credentials);
        if (threadPoolSize == 0) {
            threadPoolSize = THREADS_PER_CPU * Runtime.getRuntime().availableProcessors();
        }
        executorProvider = FixedExecutorProvider
                .create(Executors.newScheduledThreadPool(threadPoolSize, ThingsBoardThreadFactory.forName("pubsub-queue-executor")));
    }

    @PreDestroy
    private void destroy() {
        if (executorProvider != null) {
            executorProvider.getExecutor().shutdownNow();
        }
    }
}
