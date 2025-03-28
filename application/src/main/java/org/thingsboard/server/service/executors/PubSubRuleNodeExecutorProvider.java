
package org.thingsboard.server.service.executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.ExecutorProvider;
import org.thingsboard.common.util.ThingsBoardThreadFactory;
import org.thingsboard.server.queue.util.TbRuleEngineComponent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Lazy
@TbRuleEngineComponent
@Component
public class PubSubRuleNodeExecutorProvider implements ExecutorProvider {

    @Value("${service.rule_engine.pubsub.executor_thread_pool_size}")
    private Integer threadPoolSize;

    /**
    * Refers to com.google.cloud.pubsub.v1.Publisher default executor configuration
    */
    private static final int THREADS_PER_CPU = 5;
    private ScheduledExecutorService executor;

    @PostConstruct
    public void init() {
        if (threadPoolSize == null) {
            threadPoolSize = THREADS_PER_CPU * Runtime.getRuntime().availableProcessors();
        }
        executor = Executors.newScheduledThreadPool(threadPoolSize, ThingsBoardThreadFactory.forName("pubsub-rule-nodes"));
    }

    @Override
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    @PreDestroy
    private void destroy() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}
