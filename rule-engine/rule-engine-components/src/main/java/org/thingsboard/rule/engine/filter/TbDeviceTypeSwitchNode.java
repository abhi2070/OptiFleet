
package org.thingsboard.rule.engine.filter;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.rule.engine.api.EmptyNodeConfiguration;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.plugin.ComponentType;

@Slf4j
@RuleNode(
        type = ComponentType.FILTER,
        name = "device profile switch",
        customRelations = true,
        relationTypes = {"default"},
        configClazz = EmptyNodeConfiguration.class,
        nodeDescription = "Route incoming messages based on the name of the device profile",
        nodeDetails = "Route incoming messages based on the name of the device profile. The device profile name is case-sensitive<br><br>" +
                "Output connections: <i>Device profile name</i> or <code>Failure</code>",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbNodeEmptyConfig")
public class TbDeviceTypeSwitchNode extends TbAbstractTypeSwitchNode {

    @Override
    protected String getRelationType(TbContext ctx, EntityId originator) throws TbNodeException {
        if (!EntityType.DEVICE.equals(originator.getEntityType())) {
            throw new TbNodeException("Unsupported originator type: " + originator.getEntityType().getNormalName() +
                    "! Only " + EntityType.DEVICE.getNormalName() + " type is allowed.");
        }
        DeviceProfile deviceProfile = ctx.getDeviceProfileCache().get(ctx.getTenantId(), (DeviceId) originator);
        if (deviceProfile == null) {
            throw new TbNodeException("Device profile for entity id: " + originator.getId() + " wasn't found!");
        }
        return deviceProfile.getName();
    }

}
