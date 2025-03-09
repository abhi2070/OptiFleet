
package org.thingsboard.server.service.edge.rpc.processor.alarm;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessorFactory;

@Component
@TbCoreComponent
public class AlarmEdgeProcessorFactory extends BaseEdgeProcessorFactory<AlarmEdgeProcessorV1, AlarmEdgeProcessorV2> {

}
