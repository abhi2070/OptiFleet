
package org.thingsboard.server.service.edge.rpc.utils;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;

@Slf4j
public final class EdgeVersionUtils {

    public static boolean isEdgeVersionOlderThan(EdgeVersion currentVersion, EdgeVersion requiredVersion) {
        return currentVersion.ordinal() < requiredVersion.ordinal();
    }
}
