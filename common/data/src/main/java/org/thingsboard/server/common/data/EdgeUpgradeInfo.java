
package org.thingsboard.server.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EdgeUpgradeInfo {
    private boolean requiresUpdateDb;
    private String nextEdgeVersion;
}
