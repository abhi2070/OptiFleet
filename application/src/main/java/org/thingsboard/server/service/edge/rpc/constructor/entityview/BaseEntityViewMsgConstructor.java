
package org.thingsboard.server.service.edge.rpc.constructor.entityview;

import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.gen.edge.v1.EntityViewUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;

public abstract class BaseEntityViewMsgConstructor implements EntityViewMsgConstructor {

    @Override
    public EntityViewUpdateMsg constructEntityViewDeleteMsg(EntityViewId entityViewId) {
        return EntityViewUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(entityViewId.getId().getMostSignificantBits())
                .setIdLSB(entityViewId.getId().getLeastSignificantBits()).build();
    }
}
