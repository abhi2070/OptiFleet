
package org.thingsboard.server.service.edge.rpc.constructor.user;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.gen.edge.v1.UserCredentialsUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UserUpdateMsg;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface UserMsgConstructor extends MsgConstructor {

    UserUpdateMsg constructUserUpdatedMsg(UpdateMsgType msgType, User user);

    UserUpdateMsg constructUserDeleteMsg(UserId userId);

    UserCredentialsUpdateMsg constructUserCredentialsUpdatedMsg(UserCredentials userCredentials);
}
