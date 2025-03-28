
package org.thingsboard.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.dao.edge.EdgeEventService;

@AllArgsConstructor
@Slf4j
public class GeneralEdgeEventFetcher implements EdgeEventFetcher {

    private final Long queueStartTs;
    private Long seqIdStart;
    @Getter
    private Long seqIdEnd;
    @Getter
    private boolean seqIdNewCycleStarted;
    private Long maxReadRecordsCount;
    private final EdgeEventService edgeEventService;

    @Override
    public PageLink getPageLink(int pageSize) {
        return new TimePageLink(
                pageSize,
                0,
                null,
                null,
                queueStartTs,
                System.currentTimeMillis());
    }

    @Override
    public PageData<EdgeEvent> fetchEdgeEvents(TenantId tenantId, Edge edge, PageLink pageLink) {
        try {
            PageData<EdgeEvent> edgeEvents = edgeEventService.findEdgeEvents(tenantId, edge.getId(), seqIdStart, seqIdEnd, (TimePageLink) pageLink);
            if (edgeEvents.getData().isEmpty()) {
                this.seqIdEnd = Math.max(this.maxReadRecordsCount, seqIdStart - this.maxReadRecordsCount);
                edgeEvents = edgeEventService.findEdgeEvents(tenantId, edge.getId(), 0L, seqIdEnd, (TimePageLink) pageLink);
                if (edgeEvents.getData().stream().anyMatch(ee -> ee.getSeqId() < seqIdStart)) {
                    log.info("[{}] seqId column of edge_event table started new cycle [{}]", tenantId, edge.getId());
                    this.seqIdNewCycleStarted = true;
                    this.seqIdStart = 0L;
                } else {
                    edgeEvents = new PageData<>();
                    log.warn("[{}] unexpected edge notification message received. " +
                            "no new events found and seqId column of edge_event table doesn't started new cycle [{}]", tenantId, edge.getId());
                }
            }
            return edgeEvents;
        } catch (Exception e) {
            log.error("[{}] failed to find edge events [{}]", tenantId, edge.getId());
        }
        return new PageData<>();
    }
}
