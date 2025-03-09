
package org.thingsboard.server.service.executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.AbstractListeningExecutor;

@Component
public class VersionControlExecutor extends AbstractListeningExecutor {

    @Value("${vc.thread_pool_size:6}")
    private int threadPoolSize;

    @Override
    protected int getThreadPollSize() {
        return threadPoolSize;
    }
}
