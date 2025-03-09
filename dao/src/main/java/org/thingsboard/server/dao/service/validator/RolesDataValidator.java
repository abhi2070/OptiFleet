package org.thingsboard.server.dao.service.validator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.roles.RolesDao;
import org.thingsboard.server.dao.roles.BaseRolesService;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantService;




@Component
public class RolesDataValidator extends DataValidator<Roles> {
    @Autowired
    private RolesDao rolesDao;

    @Autowired
    @Lazy
    private TenantService tenantService;



    @Override
    protected void validateCreate(TenantId tenantId, Roles roles) {
        if (!BaseRolesService.TB_SERVICE_QUEUE.equals(roles.getType())) {
            validateNumberOfEntitiesPerTenant(tenantId, EntityType.ROLES);
        }
    }

    @Override
    protected Roles validateUpdate(TenantId tenantId, Roles roles) {
        Roles old = rolesDao.findById(roles.getTenantId(), roles.getId().getId());
        if (old == null) {
            throw new DataValidationException("Can't update non existing roles!");
        }
        return old;
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Roles roles) {
        validateString("Roles name", roles.getName());
        if (roles.getTenantId() == null) {
            throw new DataValidationException("Roles should be assigned to tenant!");
        } else {
            if (!tenantService.tenantExists(roles.getTenantId())) {
                throw new DataValidationException("Roles is referencing to non-existent tenant!");
            }
        }

    }
}
