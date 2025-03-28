
package org.thingsboard.rule.engine.action;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;

@Slf4j
@RuleNode(
        type = ComponentType.ACTION,
        name = "assign to customer",
        configClazz = TbAssignToCustomerNodeConfiguration.class,
        nodeDescription = "Assign Message Originator Entity to Customer",
        nodeDetails = "Finds target Customer by customer name pattern and then assign Originator Entity to this customer. " +
                "Will create new Customer if it doesn't exists and 'Create new Customer if not exists' is set to true.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbActionNodeAssignToCustomerConfig",
        icon = "add_circle"
)
public class TbAssignToCustomerNode extends TbAbstractCustomerActionNode<TbAssignToCustomerNodeConfiguration> {

    @Override
    protected boolean createCustomerIfNotExists() {
        return config.isCreateCustomerIfNotExists();
    }

    @Override
    protected TbAssignToCustomerNodeConfiguration loadCustomerNodeActionConfig(TbNodeConfiguration configuration) throws TbNodeException {
        return TbNodeUtils.convert(configuration, TbAssignToCustomerNodeConfiguration.class);
    }

    @Override
    protected void doProcessCustomerAction(TbContext ctx, TbMsg msg, CustomerId customerId) {
        processAssign(ctx, msg, customerId);
    }

    private void processAssign(TbContext ctx, TbMsg msg, CustomerId customerId) {
        EntityType originatorType = msg.getOriginator().getEntityType();
        switch (originatorType) {
            case DEVICE:
                processAssignDevice(ctx, msg, customerId);
                break;
            case ASSET:
                processAssignAsset(ctx, msg, customerId);
                break;
            case ENTITY_VIEW:
                processAssignEntityView(ctx, msg, customerId);
                break;
            case EDGE:
                processAssignEdge(ctx, msg, customerId);
                break;
            case DASHBOARD:
                processAssignDashboard(ctx, msg, customerId);
                break;
            default:
                ctx.tellFailure(msg, new RuntimeException("Unsupported originator type '" + originatorType +
                        "'! Only 'DEVICE', 'ASSET',  'ENTITY_VIEW' or 'DASHBOARD' types are allowed."));
                break;
        }
    }

    private void processAssignAsset(TbContext ctx, TbMsg msg, CustomerId customerId) {
        ctx.getAssetService().assignAssetToCustomer(ctx.getTenantId(), new AssetId(msg.getOriginator().getId()), customerId);
    }

    private void processAssignDevice(TbContext ctx, TbMsg msg, CustomerId customerId) {
        ctx.getDeviceService().assignDeviceToCustomer(ctx.getTenantId(), new DeviceId(msg.getOriginator().getId()), customerId);
    }

    private void processAssignEntityView(TbContext ctx, TbMsg msg, CustomerId customerId) {
        ctx.getEntityViewService().assignEntityViewToCustomer(ctx.getTenantId(), new EntityViewId(msg.getOriginator().getId()), customerId);
    }

    private void processAssignEdge(TbContext ctx, TbMsg msg, CustomerId customerId) {
        ctx.getEdgeService().assignEdgeToCustomer(ctx.getTenantId(), new EdgeId(msg.getOriginator().getId()), customerId);
    }

    private void processAssignDashboard(TbContext ctx, TbMsg msg, CustomerId customerId) {
        ctx.getDashboardService().assignDashboardToCustomer(ctx.getTenantId(), new DashboardId(msg.getOriginator().getId()), customerId);
    }

}
