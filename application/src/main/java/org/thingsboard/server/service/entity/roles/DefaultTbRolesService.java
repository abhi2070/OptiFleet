package org.thingsboard.server.service.entity.roles;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.dao.model.sql.RolesEntity;
import org.thingsboard.server.dao.roles.RolesService;
import org.thingsboard.server.dao.sql.roles.RolesRepository;
import org.thingsboard.server.service.entity.AbstractTbEntityService;

import static org.thingsboard.server.dao.roles.BaseRolesService.DUPLICATE_MESSAGE;
import static org.thingsboard.server.dao.roles.BaseRolesService.TB_SERVICE_QUEUE;

@Service
@AllArgsConstructor
public class DefaultTbRolesService extends AbstractTbEntityService implements TbRolesService{

    private static final Logger log = LoggerFactory.getLogger(DefaultTbRolesService.class);
    private final RolesService rolesService;
    private final RolesRepository rolesRepository;

    @Override
    @Transactional
    public Roles save(Roles roles, User user) throws Exception {
        ActionType actionType = roles.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = roles.getTenantId();

        if(actionType==ActionType.ADDED){
        RolesEntity existingRoleEntity = rolesRepository.findByName(roles.getName());

        if (existingRoleEntity != null) {
            if (roles.getId() == null || !existingRoleEntity.getId().equals(roles.getId())) {
                throw new ThingsboardException("Role with " + DUPLICATE_MESSAGE, ThingsboardErrorCode.DUPLICATE_ENTRY);
            }
        }}

        try {
            if (TB_SERVICE_QUEUE.equals(roles.getType())) {
                throw new ThingsboardException("Unable to save roles with type " + TB_SERVICE_QUEUE, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }

            Roles savedRoles = checkNotNull(rolesService.saveRoles(roles));
            autoCommit(user, savedRoles.getId());

            return savedRoles;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.ROLES), roles, actionType, user, e);
            throw e;
        }
    }



    @Override
    @Transactional
    public void delete(Roles roles, User user) {
        ActionType actionType = ActionType.DELETED;
        TenantId tenantId = roles.getTenantId();
        RolesId rolesId = roles.getId();

        try {
            removeAlarmsByEntityId(tenantId, rolesId);
            rolesService.deleteRoles(tenantId, rolesId);
        } catch (Exception e) {
           notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.ROLES), actionType, user, e,
                    rolesId.toString());
            throw e;
        }

    }

}
