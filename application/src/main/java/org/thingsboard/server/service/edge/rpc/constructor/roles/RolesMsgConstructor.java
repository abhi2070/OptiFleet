package org.thingsboard.server.service.edge.rpc.constructor.roles;


import org.thingsboard.server.common.data.id.RolesId;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.gen.edge.v1.RolesUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface RolesMsgConstructor extends MsgConstructor {

    RolesUpdateMsg constructRolesUpdatedMsg(UpdateMsgType msgType, Roles roles);

    RolesUpdateMsg constructRolesDeleteMsg(RolesId rolesId);

}
