
package org.thingsboard.monitoring.data.cmd;

import lombok.Data;
import org.thingsboard.server.common.data.query.EntityDataQuery;

@Data
public class EntityDataCmd {

    private int cmdId;
    private EntityDataQuery query;
    private LatestValueCmd latestCmd;

}
