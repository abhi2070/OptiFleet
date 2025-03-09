
package org.thingsboard.server.service.edge.rpc.processor.device.profile;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessorFactory;

@Component
@TbCoreComponent
public class DeviceProfileEdgeProcessorFactory extends BaseEdgeProcessorFactory<DeviceProfileEdgeProcessorV1, DeviceProfileEdgeProcessorV2> {

}
