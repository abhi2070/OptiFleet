package org.thingsboard.server.service.edge.rpc.constructor.roles;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.gen.edge.v1.RolesUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class RolesMsgConstructorV2 extends BaseRolesMsgConstructor {
    @Override
    public RolesUpdateMsg constructRolesUpdatedMsg(UpdateMsgType msgType, Roles roles) {
        return RolesUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(roles))
                .setIdMSB(roles.getUuidId().getMostSignificantBits())
                .setIdLSB(roles.getUuidId().getLeastSignificantBits()).build();
    }


}
