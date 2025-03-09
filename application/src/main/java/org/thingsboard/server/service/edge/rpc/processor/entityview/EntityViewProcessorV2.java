
package org.thingsboard.server.service.edge.rpc.processor.entityview;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.EntityViewUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Primary
@Component
@TbCoreComponent
public class EntityViewProcessorV2 extends EntityViewEdgeProcessor {

    @Override
    protected EntityView constructEntityViewFromUpdateMsg(TenantId tenantId, EntityViewId entityViewId, EntityViewUpdateMsg entityViewUpdateMsg) {
        return JacksonUtil.fromString(entityViewUpdateMsg.getEntity(), EntityView.class, true);
    }

    @Override
    protected void setCustomerId(TenantId tenantId, CustomerId customerId, EntityView entityView, EntityViewUpdateMsg entityViewUpdateMsg) {
        CustomerId customerUUID = entityView.getCustomerId() != null ? entityView.getCustomerId() : customerId;
        entityView.setCustomerId(customerUUID);
    }
}
