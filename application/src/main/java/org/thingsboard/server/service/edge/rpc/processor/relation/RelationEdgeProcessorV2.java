
package org.thingsboard.server.service.edge.rpc.processor.relation;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.gen.edge.v1.RelationUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Primary
@Component
@TbCoreComponent
public class RelationEdgeProcessorV2 extends RelationEdgeProcessor {

    @Override
    protected EntityRelation constructEntityRelationFromUpdateMsg(RelationUpdateMsg relationUpdateMsg) {
        return JacksonUtil.fromString(relationUpdateMsg.getEntity(), EntityRelation.class, true);
    }
}
