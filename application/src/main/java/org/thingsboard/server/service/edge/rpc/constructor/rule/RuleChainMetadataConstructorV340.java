
package org.thingsboard.server.service.edge.rpc.constructor.rule;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.rule.RuleChainMetaData;
import org.thingsboard.server.gen.edge.v1.RuleChainMetadataUpdateMsg;

import java.util.TreeSet;

@Slf4j
public class RuleChainMetadataConstructorV340 extends BaseRuleChainMetadataConstructor {

    @Override
    protected void constructRuleChainMetadataUpdatedMsg(TenantId tenantId,
                                                        RuleChainMetadataUpdateMsg.Builder builder,
                                                        RuleChainMetaData ruleChainMetaData) {
        builder.setRuleChainIdMSB(ruleChainMetaData.getRuleChainId().getId().getMostSignificantBits())
                .setRuleChainIdLSB(ruleChainMetaData.getRuleChainId().getId().getLeastSignificantBits());
        builder.addAllNodes(constructNodes(ruleChainMetaData.getNodes()))
                .addAllConnections(constructConnections(ruleChainMetaData.getConnections()))
                .addAllRuleChainConnections(constructRuleChainConnections(ruleChainMetaData.getRuleChainConnections(), new TreeSet<>()));
        if (ruleChainMetaData.getFirstNodeIndex() != null) {
            builder.setFirstNodeIndex(ruleChainMetaData.getFirstNodeIndex());
        } else {
            builder.setFirstNodeIndex(-1);
        }
    }
}
