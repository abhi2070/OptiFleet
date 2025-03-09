
package org.thingsboard.server.service.edge.rpc.processor.device;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessorFactory;

@Component
@TbCoreComponent
public class DeviceEdgeProcessorFactory extends BaseEdgeProcessorFactory<DeviceEdgeProcessorV1, DeviceEdgeProcessorV2> {

}
