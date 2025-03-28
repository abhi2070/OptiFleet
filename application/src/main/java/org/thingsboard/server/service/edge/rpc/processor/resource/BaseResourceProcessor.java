
package org.thingsboard.server.service.edge.rpc.processor.resource;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.ResourceType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.TbResourceInfo;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageDataIterable;
import org.thingsboard.server.gen.edge.v1.ResourceUpdateMsg;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;

@Slf4j
public abstract class BaseResourceProcessor extends BaseEdgeProcessor {

    protected boolean saveOrUpdateTbResource(TenantId tenantId, TbResourceId tbResourceId, ResourceUpdateMsg resourceUpdateMsg) {
        boolean resourceKeyUpdated = false;
        try {
            TbResource resource = constructResourceFromUpdateMsg(tenantId, tbResourceId, resourceUpdateMsg);
            if (resource == null) {
                throw new RuntimeException("[{" + tenantId + "}] resourceUpdateMsg {" + resourceUpdateMsg + " } cannot be converted to resource");
            }
            boolean created = false;
            TbResource resourceById = resourceService.findResourceById(tenantId, tbResourceId);
            if (resourceById == null) {
                resource.setCreatedTime(Uuids.unixTimestamp(tbResourceId.getId()));
                created = true;
                resource.setId(null);
            } else {
                resource.setId(tbResourceId);
            }
            String resourceKey = resource.getResourceKey();
            ResourceType resourceType = resource.getResourceType();
            PageDataIterable<TbResource> resourcesIterable = new PageDataIterable<>(
                    link -> resourceService.findTenantResourcesByResourceTypeAndPageLink(tenantId, resourceType, link), 1024);
            for (TbResource tbResource : resourcesIterable) {
                if (tbResource.getResourceKey().equals(resourceKey) && !tbResourceId.equals(tbResource.getId())) {
                    resourceKey = StringUtils.randomAlphabetic(15) + "_" + resourceKey;
                    log.warn("[{}] Resource with resource type {} and key {} already exists. Renaming resource key to {}",
                            tenantId, resourceType, resource.getResourceKey(), resourceKey);
                    resourceKeyUpdated = true;
                }
            }
            resource.setResourceKey(resourceKey);
            resourceValidator.validate(resource, TbResourceInfo::getTenantId);
            if (created) {
                resource.setId(tbResourceId);
            }
            resourceService.saveResource(resource, false);
        } catch (Exception e) {
            log.error("[{}] Failed to process resource update msg [{}]", tenantId, resourceUpdateMsg, e);
            throw e;
        }
        return resourceKeyUpdated;
    }

    protected abstract TbResource constructResourceFromUpdateMsg(TenantId tenantId, TbResourceId tbResourceId, ResourceUpdateMsg resourceUpdateMsg);
}
