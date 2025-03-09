
package org.thingsboard.server.service.edge.rpc.constructor.relation;

import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.gen.edge.v1.RelationUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface RelationMsgConstructor extends MsgConstructor {

    RelationUpdateMsg constructRelationUpdatedMsg(UpdateMsgType msgType, EntityRelation entityRelation);
}
