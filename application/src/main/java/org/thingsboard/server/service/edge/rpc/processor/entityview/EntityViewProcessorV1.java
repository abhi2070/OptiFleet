
package org.thingsboard.server.service.edge.rpc.processor.entityview;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.EdgeEntityType;
import org.thingsboard.server.gen.edge.v1.EntityViewUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.UUID;

@Component
@TbCoreComponent
public class EntityViewProcessorV1 extends EntityViewEdgeProcessor {

    @Override
    protected EntityView constructEntityViewFromUpdateMsg(TenantId tenantId, EntityViewId entityViewId, EntityViewUpdateMsg entityViewUpdateMsg) {
        EntityView entityView = new EntityView();
        entityView.setTenantId(tenantId);
        entityView.setCreatedTime(Uuids.unixTimestamp(entityViewId.getId()));
        entityView.setName(entityViewUpdateMsg.getName());
        entityView.setType(entityViewUpdateMsg.getType());

        entityView.setAdditionalInfo(entityViewUpdateMsg.hasAdditionalInfo() ?
                JacksonUtil.toJsonNode(entityViewUpdateMsg.getAdditionalInfo()) : null);

        CustomerId customerId = safeGetCustomerId(entityViewUpdateMsg.getCustomerIdMSB(), entityViewUpdateMsg.getCustomerIdLSB());
        entityView.setCustomerId(customerId);

        UUID entityIdUUID = safeGetUUID(entityViewUpdateMsg.getEntityIdMSB(), entityViewUpdateMsg.getEntityIdLSB());
        if (EdgeEntityType.DEVICE.equals(entityViewUpdateMsg.getEntityType())) {
            entityView.setEntityId(entityIdUUID != null ? new DeviceId(entityIdUUID) : null);
        } else if (EdgeEntityType.ASSET.equals(entityViewUpdateMsg.getEntityType())) {
            entityView.setEntityId(entityIdUUID != null ? new AssetId(entityIdUUID) : null);
        }
        return entityView;
    }

    @Override
    protected void setCustomerId(TenantId tenantId, CustomerId customerId, EntityView entityView, EntityViewUpdateMsg entityViewUpdateMsg) {
        CustomerId customerUUID = safeGetCustomerId(entityViewUpdateMsg.getCustomerIdMSB(), entityViewUpdateMsg.getCustomerIdLSB());
        entityView.setCustomerId(customerUUID != null ? customerUUID : customerId);
    }
}
