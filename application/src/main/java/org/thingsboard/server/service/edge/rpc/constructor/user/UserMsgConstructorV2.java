
package org.thingsboard.server.service.edge.rpc.constructor.user;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.gen.edge.v1.UserCredentialsUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UserUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class UserMsgConstructorV2 extends BaseUserMsgConstructor {

    @Override
    public UserUpdateMsg constructUserUpdatedMsg(UpdateMsgType msgType, User user) {
        return UserUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(user))
                .setIdMSB(user.getId().getId().getMostSignificantBits())
                .setIdLSB(user.getId().getId().getLeastSignificantBits()).build();
    }

    @Override
    public UserCredentialsUpdateMsg constructUserCredentialsUpdatedMsg(UserCredentials userCredentials) {
        return UserCredentialsUpdateMsg.newBuilder().setEntity(JacksonUtil.toString(userCredentials)).build();
    }
}
