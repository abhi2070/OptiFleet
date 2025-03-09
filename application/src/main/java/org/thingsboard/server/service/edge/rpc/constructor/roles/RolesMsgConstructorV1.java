package org.thingsboard.server.service.edge.rpc.constructor.roles;

import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.dao.resource.ImageService;
import org.thingsboard.server.gen.edge.v1.RolesUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;


@Component
@TbCoreComponent
public class RolesMsgConstructorV1 extends BaseRolesMsgConstructor {
    @Autowired
    private ImageService imageService;

    @Override
    public RolesUpdateMsg constructRolesUpdatedMsg(UpdateMsgType msgType, Roles roles) {
        RolesUpdateMsg.Builder builder = RolesUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(roles.getUuidId().getMostSignificantBits())
                .setIdLSB(roles.getUuidId().getLeastSignificantBits())
                .setName(roles.getName())
                .setType(roles.getType());

        if (roles.getAdditionalInfo() != null) {
            builder.setAdditionalInfo(JacksonUtil.toString(roles.getAdditionalInfo()));
        }
        return builder.build();
    }


}
