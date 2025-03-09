
package org.thingsboard.server.service.edge.rpc.constructor.relation;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.gen.edge.v1.RelationUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class RelationMsgConstructorV2 implements RelationMsgConstructor {


    @Override
    public RelationUpdateMsg constructRelationUpdatedMsg(UpdateMsgType msgType, EntityRelation entityRelation) {
        return RelationUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(entityRelation)).build();
    }
}
