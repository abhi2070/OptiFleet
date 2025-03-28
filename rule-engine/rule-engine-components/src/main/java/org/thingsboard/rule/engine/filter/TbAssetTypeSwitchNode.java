
package org.thingsboard.rule.engine.filter;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.rule.engine.api.EmptyNodeConfiguration;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.plugin.ComponentType;

@Slf4j
@RuleNode(
        type = ComponentType.FILTER,
        name = "asset profile switch",
        customRelations = true,
        relationTypes = {"default"},
        configClazz = EmptyNodeConfiguration.class,
        nodeDescription = "Route incoming messages based on the name of the asset profile",
        nodeDetails = "Route incoming messages based on the name of the asset profile. The asset profile name is case-sensitive.<br><br>" +
                "Output connections: <i>Asset profile name</i> or <code>Failure</code>",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbNodeEmptyConfig")
public class TbAssetTypeSwitchNode extends TbAbstractTypeSwitchNode {

    @Override
    protected String getRelationType(TbContext ctx, EntityId originator) throws TbNodeException {
        if (!EntityType.ASSET.equals(originator.getEntityType())) {
            throw new TbNodeException("Unsupported originator type: " + originator.getEntityType().getNormalName() + "!" +
                    " Only " + EntityType.ASSET.getNormalName() + " type is allowed.");
        }
        AssetProfile assetProfile = ctx.getAssetProfileCache().get(ctx.getTenantId(), (AssetId) originator);
        if (assetProfile == null) {
            throw new TbNodeException("Asset profile for entity id: " + originator.getId() + " wasn't found!");
        }
        return assetProfile.getName();
    }

}
