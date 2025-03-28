
package org.thingsboard.server.service.edge.rpc.constructor.rule;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.gen.edge.v1.RuleChainUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class RuleChainMsgConstructorV2 extends BaseRuleChainMsgConstructor {

    @Override
    public RuleChainUpdateMsg constructRuleChainUpdatedMsg(UpdateMsgType msgType, RuleChain ruleChain, boolean isRoot) {
        boolean isTemplateRoot = ruleChain.isRoot();
        ruleChain.setRoot(isRoot);
        RuleChainUpdateMsg result = RuleChainUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(ruleChain))
                .setIdMSB(ruleChain.getId().getId().getMostSignificantBits())
                .setIdLSB(ruleChain.getId().getId().getLeastSignificantBits()).build();
        ruleChain.setRoot(isTemplateRoot);
        return result;
    }
}
