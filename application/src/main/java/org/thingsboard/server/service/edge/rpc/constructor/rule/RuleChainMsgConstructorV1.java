
package org.thingsboard.server.service.edge.rpc.constructor.rule;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.gen.edge.v1.RuleChainUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class RuleChainMsgConstructorV1 extends BaseRuleChainMsgConstructor {

    @Override
    public RuleChainUpdateMsg constructRuleChainUpdatedMsg(UpdateMsgType msgType, RuleChain ruleChain, boolean isRoot) {
        RuleChainUpdateMsg.Builder builder = RuleChainUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(ruleChain.getId().getId().getMostSignificantBits())
                .setIdLSB(ruleChain.getId().getId().getLeastSignificantBits())
                .setName(ruleChain.getName())
                .setRoot(isRoot)
                .setDebugMode(ruleChain.isDebugMode())
                .setConfiguration(JacksonUtil.toString(ruleChain.getConfiguration()));
        if (ruleChain.getFirstRuleNodeId() != null) {
            builder.setFirstRuleNodeIdMSB(ruleChain.getFirstRuleNodeId().getId().getMostSignificantBits())
                    .setFirstRuleNodeIdLSB(ruleChain.getFirstRuleNodeId().getId().getLeastSignificantBits());
        }
        return builder.build();
    }
}
