package org.thingsboard.server.service.edge.rpc.processor.roles;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessorFactory;

@Component
@TbCoreComponent
public class RolesEdgeProcessorFactory extends BaseEdgeProcessorFactory<RolesEdgeProcessorV1, RolesEdgeProcessorV2> {
}
