package org.thingsboard.server.service.edge.rpc.constructor.roles;

import org.thingsboard.server.common.data.id.RolesId;
import org.thingsboard.server.gen.edge.v1.RolesUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;


public abstract class BaseRolesMsgConstructor implements RolesMsgConstructor {




    @Override
    public RolesUpdateMsg constructRolesDeleteMsg(RolesId rolesId) {
        return RolesUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(rolesId.getId().getMostSignificantBits())
                .setIdLSB(rolesId.getId().getLeastSignificantBits()).build();
    }


}
