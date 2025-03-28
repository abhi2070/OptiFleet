
package org.thingsboard.server.service.edge.rpc.constructor.rule;

import org.thingsboard.server.gen.edge.v1.EdgeVersion;

public final class RuleChainMetadataConstructorFactory {

    public static RuleChainMetadataConstructor getByEdgeVersion(EdgeVersion edgeVersion) {
        switch (edgeVersion) {
            case V_3_3_0:
                return new RuleChainMetadataConstructorV330();
            case V_3_3_3:
            case V_3_4_0:
            case V_3_6_0:
            case V_3_6_1:
                return new RuleChainMetadataConstructorV340();
            case V_3_6_2:
            default:
                return new RuleChainMetadataConstructorV362();
        }
    }
}
