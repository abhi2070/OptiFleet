
package org.thingsboard.server.service.entity.entity.relation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.dao.relation.RelationService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.AbstractTbEntityService;

@Service
@TbCoreComponent
@AllArgsConstructor
@Slf4j
public class DefaultTbEntityRelationService extends AbstractTbEntityService implements TbEntityRelationService {

    private final RelationService relationService;

    @Override
    public void save(TenantId tenantId, CustomerId customerId, EntityRelation relation, User user) throws ThingsboardException {
        ActionType actionType = ActionType.RELATION_ADD_OR_UPDATE;
        try {
            relationService.saveRelation(tenantId, relation);
            notificationEntityService.logEntityRelationAction(tenantId, customerId,
                    relation, user, actionType, null, relation);
        } catch (Exception e) {
            notificationEntityService.logEntityRelationAction(tenantId, customerId,
                    relation, user, actionType, e, relation);
            throw e;
        }
    }

    @Override
    public void delete(TenantId tenantId, CustomerId customerId, EntityRelation relation, User user) throws ThingsboardException {
        ActionType actionType = ActionType.RELATION_DELETED;
        try {
            boolean found = relationService.deleteRelation(tenantId, relation.getFrom(), relation.getTo(), relation.getType(), relation.getTypeGroup());
            if (!found) {
                throw new ThingsboardException("Requested item wasn't found!", ThingsboardErrorCode.ITEM_NOT_FOUND);
            }
            notificationEntityService.logEntityRelationAction(tenantId, customerId, relation, user, actionType, null, relation);
        } catch (Exception e) {
            notificationEntityService.logEntityRelationAction(tenantId, customerId,
                    relation, user, actionType, e, relation);
            throw e;
        }
    }

    @Override
    public void deleteCommonRelations(TenantId tenantId, CustomerId customerId, EntityId entityId, User user) throws ThingsboardException {
        try {
            relationService.deleteEntityCommonRelations(tenantId, entityId);
            notificationEntityService.logEntityAction(tenantId, entityId, null, customerId, ActionType.RELATIONS_DELETED, user);
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, entityId, null, customerId,
                    ActionType.RELATIONS_DELETED, user, e);
            throw e;
        }
    }
}
