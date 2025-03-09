
package org.thingsboard.server.service.edge.rpc.processor.dashboard;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessorFactory;

@Component
@TbCoreComponent
public class DashboardEdgeProcessorFactory extends BaseEdgeProcessorFactory<DashboardEdgeProcessorV1, DashboardEdgeProcessorV2> {

}
