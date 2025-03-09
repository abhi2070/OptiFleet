
package org.thingsboard.server.service.edge.rpc.constructor.resource;

import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.gen.edge.v1.ResourceUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface ResourceMsgConstructor extends MsgConstructor {

    ResourceUpdateMsg constructResourceUpdatedMsg(UpdateMsgType msgType, TbResource tbResource);

    ResourceUpdateMsg constructResourceDeleteMsg(TbResourceId tbResourceId);
}
