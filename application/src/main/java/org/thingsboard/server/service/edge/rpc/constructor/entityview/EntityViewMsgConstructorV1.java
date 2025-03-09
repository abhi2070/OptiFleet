
package org.thingsboard.server.service.edge.rpc.constructor.entityview;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.gen.edge.v1.EdgeEntityType;
import org.thingsboard.server.gen.edge.v1.EntityViewUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class EntityViewMsgConstructorV1 extends BaseEntityViewMsgConstructor {

    @Override
    public EntityViewUpdateMsg constructEntityViewUpdatedMsg(UpdateMsgType msgType, EntityView entityView) {
        EdgeEntityType edgeEntityType = checkEntityType(entityView.getEntityId().getEntityType());
        EntityViewUpdateMsg.Builder builder = EntityViewUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(entityView.getId().getId().getMostSignificantBits())
                .setIdLSB(entityView.getId().getId().getLeastSignificantBits())
                .setName(entityView.getName())
                .setType(entityView.getType())
                .setEntityIdMSB(entityView.getEntityId().getId().getMostSignificantBits())
                .setEntityIdLSB(entityView.getEntityId().getId().getLeastSignificantBits())
                .setEntityType(edgeEntityType);
        if (entityView.getCustomerId() != null) {
            builder.setCustomerIdMSB(entityView.getCustomerId().getId().getMostSignificantBits());
            builder.setCustomerIdLSB(entityView.getCustomerId().getId().getLeastSignificantBits());
        }
        if (entityView.getAdditionalInfo() != null) {
            builder.setAdditionalInfo(JacksonUtil.toString(entityView.getAdditionalInfo()));
        }
        return builder.build();
    }

    private EdgeEntityType checkEntityType(EntityType entityType) {
        switch (entityType) {
            case DEVICE:
                return EdgeEntityType.DEVICE;
            case ASSET:
                return EdgeEntityType.ASSET;
            default:
                throw new RuntimeException("Unsupported entity type [" + entityType + "]");
        }
    }
}
