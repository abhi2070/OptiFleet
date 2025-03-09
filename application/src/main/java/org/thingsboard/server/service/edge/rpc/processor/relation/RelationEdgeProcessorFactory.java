
package org.thingsboard.server.service.edge.rpc.processor.relation;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessorFactory;

@Component
@TbCoreComponent
public class RelationEdgeProcessorFactory extends BaseEdgeProcessorFactory<RelationEdgeProcessorV1, RelationEdgeProcessorV2> {

}
