
package org.thingsboard.server.service.edge.rpc.processor.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.gen.edge.v1.UserCredentialsUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.user.UserMsgConstructor;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;

@Slf4j
@Component
@TbCoreComponent
public class UserEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg convertUserEventToDownlink(EdgeEvent edgeEvent, EdgeVersion edgeVersion) {
        UserId userId = new UserId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (edgeEvent.getAction()) {
            case ADDED:
            case UPDATED:
                User user = userService.findUserById(edgeEvent.getTenantId(), userId);
                if (user != null) {
                    UpdateMsgType msgType = getUpdateMsgType(edgeEvent.getAction());
                    downlinkMsg = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addUserUpdateMsg(((UserMsgConstructor) userMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructUserUpdatedMsg(msgType, user))
                            .build();
                }
                break;
            case DELETED:
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addUserUpdateMsg(((UserMsgConstructor) userMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructUserDeleteMsg(userId))
                        .build();
                break;
            case CREDENTIALS_UPDATED:
                UserCredentials userCredentialsByUserId = userService.findUserCredentialsByUserId(edgeEvent.getTenantId(), userId);
                if (userCredentialsByUserId != null && userCredentialsByUserId.isEnabled()) {
                    UserCredentialsUpdateMsg userCredentialsUpdateMsg =
                            ((UserMsgConstructor) userMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructUserCredentialsUpdatedMsg(userCredentialsByUserId);
                    downlinkMsg = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addUserCredentialsUpdateMsg(userCredentialsUpdateMsg)
                            .build();
                }
                break;
        }
        return downlinkMsg;
    }
}
