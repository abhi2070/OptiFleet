
package org.thingsboard.server.service.edge.rpc.processor.asset.profile;

import org.springframework.stereotype.Component;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessorFactory;

@Component
@TbCoreComponent
public class AssetProfileEdgeProcessorFactory extends BaseEdgeProcessorFactory<AssetProfileEdgeProcessorV1, AssetProfileEdgeProcessorV2> {

}
