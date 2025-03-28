
package org.thingsboard.server.dao.aspect;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class MethodCallStats {
    private final AtomicInteger executions = new AtomicInteger();
    private final AtomicInteger failures = new AtomicInteger();
    private final AtomicLong timing = new AtomicLong();

    public MethodCallStatsSnapshot snapshot() {
        return new MethodCallStatsSnapshot(executions.get(), failures.get(), timing.get());
    }

}
