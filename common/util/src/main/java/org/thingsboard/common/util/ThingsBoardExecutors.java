
package org.thingsboard.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class ThingsBoardExecutors {

    /**
     * Method forked from ExecutorService to provide thread poll name
     *
     * Creates a thread pool that maintains enough threads to support
     * the given parallelism level, and may use multiple queues to
     * reduce contention. The parallelism level corresponds to the
     * maximum number of threads actively engaged in, or available to
     * engage in, task processing. The actual number of threads may
     * grow and shrink dynamically. A work-stealing pool makes no
     * guarantees about the order in which submitted tasks are
     * executed.
     *
     * @param parallelism the targeted parallelism level
     * @param namePrefix used to define thread name
     * @return the newly created thread pool
     * @throws IllegalArgumentException if {@code parallelism <= 0}
     * @since 1.8
     */
    public static ExecutorService newWorkStealingPool(int parallelism, String namePrefix) {
        return new ForkJoinPool(parallelism,
                new ThingsBoardForkJoinWorkerThreadFactory(namePrefix),
                null, true);
    }

    public static ExecutorService newWorkStealingPool(int parallelism, Class clazz) {
        return newWorkStealingPool(parallelism, clazz.getSimpleName());
    }

}
