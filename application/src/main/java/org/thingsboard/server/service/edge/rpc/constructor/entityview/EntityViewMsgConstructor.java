
package org.thingsboard.server.service.edge.rpc.constructor.entityview;

import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.gen.edge.v1.EntityViewUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface EntityViewMsgConstructor extends MsgConstructor {

    EntityViewUpdateMsg constructEntityViewUpdatedMsg(UpdateMsgType msgType, EntityView entityView);

    EntityViewUpdateMsg constructEntityViewDeleteMsg(EntityViewId entityViewId);
}
