
package org.thingsboard.server.dao.edge;

import org.thingsboard.server.common.data.id.EdgeId;

public interface EdgeSynchronizationManager {

    ThreadLocal<EdgeId> getEdgeId();
}
