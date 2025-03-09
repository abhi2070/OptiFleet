
package org.thingsboard.server.dao.edge;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.EdgeId;

@Component
@Slf4j
public class DefaultEdgeSynchronizationManager implements EdgeSynchronizationManager {

    @Getter
    private final ThreadLocal<EdgeId> edgeId = new ThreadLocal<>();
}
