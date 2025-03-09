
package org.thingsboard.server.service.edge.rpc.processor.resource;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.ResourceType;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.ResourceUpdateMsg;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class ResourceEdgeProcessorV1 extends ResourceEdgeProcessor {

    @Override
    protected TbResource constructResourceFromUpdateMsg(TenantId tenantId, TbResourceId tbResourceId, ResourceUpdateMsg resourceUpdateMsg) {
        TbResource resource = new TbResource();
        if (resourceUpdateMsg.getIsSystem()) {
            resource.setTenantId(TenantId.SYS_TENANT_ID);
        } else {
            resource.setTenantId(tenantId);
        }
        resource.setCreatedTime(Uuids.unixTimestamp(tbResourceId.getId()));
        resource.setTitle(resourceUpdateMsg.getTitle());
        resource.setResourceKey(resourceUpdateMsg.getResourceKey());
        resource.setResourceType(ResourceType.valueOf(resourceUpdateMsg.getResourceType()));
        resource.setFileName(resourceUpdateMsg.getFileName());
        resource.setEncodedData(resourceUpdateMsg.hasData() ? resourceUpdateMsg.getData() : null);
        resource.setEtag(resourceUpdateMsg.hasEtag() ? resourceUpdateMsg.getEtag() : null);
        return resource;
    }
}
