
package org.thingsboard.server.service.edge.rpc.constructor.user;

import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.gen.edge.v1.UserUpdateMsg;

public abstract class BaseUserMsgConstructor implements UserMsgConstructor {

    @Override
    public UserUpdateMsg constructUserDeleteMsg(UserId userId) {
        return UserUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(userId.getId().getMostSignificantBits())
                .setIdLSB(userId.getId().getLeastSignificantBits()).build();
    }
}
