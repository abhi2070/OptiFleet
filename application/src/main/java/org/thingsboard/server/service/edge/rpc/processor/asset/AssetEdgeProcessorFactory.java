
package org.thingsboard.server.service.edge.rpc.processor.asset;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessorFactory;

@Component
@TbCoreComponent
public class AssetEdgeProcessorFactory extends BaseEdgeProcessorFactory<AssetEdgeProcessorV1, AssetEdgeProcessorV2> {

}
