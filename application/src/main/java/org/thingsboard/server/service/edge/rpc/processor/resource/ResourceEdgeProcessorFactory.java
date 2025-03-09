
package org.thingsboard.server.service.edge.rpc.processor.resource;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessorFactory;

@Component
@TbCoreComponent
public class ResourceEdgeProcessorFactory extends BaseEdgeProcessorFactory<ResourceEdgeProcessorV1, ResourceEdgeProcessorV2> {

}
