
package org.thingsboard.server.dao.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.ThingsBoardThreadFactory;
import org.thingsboard.server.common.stats.MessagesStats;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TbSqlBlockingQueue<E> implements TbSqlQueue<E> {

    private final BlockingQueue<TbSqlQueueElement<E>> queue = new LinkedBlockingQueue<>();
    private final TbSqlBlockingQueueParams params;

    private ExecutorService executor;
    private final MessagesStats stats;

    public TbSqlBlockingQueue(TbSqlBlockingQueueParams params, MessagesStats stats) {
        this.params = params;
        this.stats = stats;
    }

    @Override
    public void init(ScheduledLogExecutorComponent logExecutor, Consumer<List<E>> saveFunction, Comparator<E> batchUpdateComparator, int index) {
        executor = Executors.newSingleThreadExecutor(ThingsBoardThreadFactory.forName("sql-queue-" + index + "-" + params.getLogName().toLowerCase()));
        executor.submit(() -> {
            String logName = params.getLogName();
            int batchSize = params.getBatchSize();
            long maxDelay = params.getMaxDelay();
            final List<TbSqlQueueElement<E>> entities = new ArrayList<>(batchSize);
            while (!Thread.interrupted()) {
                try {
                    long currentTs = System.currentTimeMillis();
                    TbSqlQueueElement<E> attr = queue.poll(maxDelay, TimeUnit.MILLISECONDS);
                    if (attr == null) {
                        continue;
                    } else {
                        entities.add(attr);
                    }
                    queue.drainTo(entities, batchSize - 1);
                    boolean fullPack = entities.size() == batchSize;
                    if (log.isDebugEnabled()) {
                        log.debug("[{}] Going to save {} entities", logName, entities.size());
                        log.trace("[{}] Going to save entities: {}", logName, entities);
                    }
                    Stream<E> entitiesStream = entities.stream().map(TbSqlQueueElement::getEntity);
                    saveFunction.accept(
                            (params.isBatchSortEnabled() ? entitiesStream.sorted(batchUpdateComparator) : entitiesStream)
                                    .collect(Collectors.toList())
                    );
                    entities.forEach(v -> v.getFuture().set(null));
                    stats.incrementSuccessful(entities.size());
                    if (!fullPack) {
                        long remainingDelay = maxDelay - (System.currentTimeMillis() - currentTs);
                        if (remainingDelay > 0) {
                            Thread.sleep(remainingDelay);
                        }
                    }
                } catch (Throwable t) {
                    if (t instanceof InterruptedException) {
                        log.info("[{}] Queue polling was interrupted", logName);
                        break;
                    } else {
                        log.error("[{}] Failed to save {} entities", logName, entities.size(), t);
                        try {
                            stats.incrementFailed(entities.size());
                            entities.forEach(entityFutureWrapper -> entityFutureWrapper.getFuture().setException(t));
                        } catch (Throwable th) {
                            log.error("[{}] Failed to set future exception", logName, th);
                        }
                    }
                } finally {
                    entities.clear();
                }
            }
            log.info("[{}] Queue polling completed", logName);
        });

        logExecutor.scheduleAtFixedRate(() -> {
            if (queue.size() > 0 || stats.getTotal() > 0 || stats.getSuccessful() > 0 || stats.getFailed() > 0) {
                log.info("Queue-{} [{}] queueSize [{}] totalAdded [{}] totalSaved [{}] totalFailed [{}]", index,
                        params.getLogName(), queue.size(), stats.getTotal(), stats.getSuccessful(), stats.getFailed());
                stats.reset();
            }
        }, params.getStatsPrintIntervalMs(), params.getStatsPrintIntervalMs(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Override
    public ListenableFuture<Void> add(E element) {
        SettableFuture<Void> future = SettableFuture.create();
        queue.add(new TbSqlQueueElement<>(future, element));
        stats.incrementTotal();
        return future;
    }
}
