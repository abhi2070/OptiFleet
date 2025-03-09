
package org.thingsboard.server.service.edge.rpc.processor.resource;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.ResourceUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Primary
@Component
@TbCoreComponent
public class ResourceEdgeProcessorV2 extends ResourceEdgeProcessor {

    @Override
    protected TbResource constructResourceFromUpdateMsg(TenantId tenantId, TbResourceId tbResourceId, ResourceUpdateMsg resourceUpdateMsg) {
        return JacksonUtil.fromString(resourceUpdateMsg.getEntity(), TbResource.class, true);
    }
}
