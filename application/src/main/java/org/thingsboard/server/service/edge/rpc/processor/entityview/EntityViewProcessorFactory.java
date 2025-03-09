
package org.thingsboard.server.service.edge.rpc.processor.entityview;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessorFactory;

@Component
@TbCoreComponent
public class EntityViewProcessorFactory extends BaseEdgeProcessorFactory<EntityViewProcessorV1, EntityViewProcessorV2> {

}
