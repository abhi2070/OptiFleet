
package org.thingsboard.server.service.edge.rpc.processor.entityview;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.EntityViewUpdateMsg;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;

@Slf4j
public abstract class BaseEntityViewProcessor extends BaseEdgeProcessor {

    protected Pair<Boolean, Boolean> saveOrUpdateEntityView(TenantId tenantId, EntityViewId entityViewId, EntityViewUpdateMsg entityViewUpdateMsg) {
        boolean created = false;
        boolean entityViewNameUpdated = false;
        EntityView entityView = constructEntityViewFromUpdateMsg(tenantId, entityViewId, entityViewUpdateMsg);
        if (entityView == null) {
            throw new RuntimeException("[{" + tenantId + "}] entityViewUpdateMsg {" + entityViewUpdateMsg + "} cannot be converted to entity view");
        }
        EntityView entityViewById = entityViewService.findEntityViewById(tenantId, entityViewId);
        if (entityViewById == null) {
            created = true;
            entityView.setId(null);
        } else {
            entityView.setId(entityViewId);
        }
        String entityViewName = entityView.getName();
        EntityView entityViewByName = entityViewService.findEntityViewByTenantIdAndName(tenantId, entityViewName);
        if (entityViewByName != null && !entityViewByName.getId().equals(entityViewId)) {
            entityViewName = entityViewName + "_" + StringUtils.randomAlphanumeric(15);
            log.warn("[{}] Entity view with name {} already exists. Renaming entity view name to {}",
                    tenantId, entityView.getName(), entityViewName);
            entityViewNameUpdated = true;
        }
        entityView.setName(entityViewName);
        setCustomerId(tenantId, created ? null : entityViewById.getCustomerId(), entityView, entityViewUpdateMsg);

        entityViewValidator.validate(entityView, EntityView::getTenantId);
        if (created) {
            entityView.setId(entityViewId);
        }
        entityViewService.saveEntityView(entityView, false);
        return Pair.of(created, entityViewNameUpdated);
    }

    protected abstract EntityView constructEntityViewFromUpdateMsg(TenantId tenantId, EntityViewId entityViewId, EntityViewUpdateMsg entityViewUpdateMsg);

    protected abstract void setCustomerId(TenantId tenantId, CustomerId customerId, EntityView entityView, EntityViewUpdateMsg entityViewUpdateMsg);
}
