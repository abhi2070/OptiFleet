
package org.thingsboard.server.service.edge.rpc.constructor.rule;

import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainMetaData;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.RuleChainMetadataUpdateMsg;
import org.thingsboard.server.gen.edge.v1.RuleChainUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface RuleChainMsgConstructor extends MsgConstructor {

    RuleChainUpdateMsg constructRuleChainUpdatedMsg(UpdateMsgType msgType, RuleChain ruleChain, boolean isRoot);

    RuleChainUpdateMsg constructRuleChainDeleteMsg(RuleChainId ruleChainId);

    RuleChainMetadataUpdateMsg constructRuleChainMetadataUpdatedMsg(TenantId tenantId,
                                                                    UpdateMsgType msgType,
                                                                    RuleChainMetaData ruleChainMetaData,
                                                                    EdgeVersion edgeVersion);
}
