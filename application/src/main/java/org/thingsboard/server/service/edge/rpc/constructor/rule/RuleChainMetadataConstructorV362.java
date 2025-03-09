
package org.thingsboard.server.service.edge.rpc.constructor.rule;

import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.rule.RuleChainMetaData;
import org.thingsboard.server.gen.edge.v1.RuleChainMetadataUpdateMsg;

public class RuleChainMetadataConstructorV362 extends BaseRuleChainMetadataConstructor {

    @Override
    protected void constructRuleChainMetadataUpdatedMsg(TenantId tenantId, RuleChainMetadataUpdateMsg.Builder builder, RuleChainMetaData ruleChainMetaData) {
        builder.setEntity(JacksonUtil.toString(ruleChainMetaData));
    }
}
