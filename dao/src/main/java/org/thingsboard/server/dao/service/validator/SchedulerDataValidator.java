package org.thingsboard.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.scheduler.BaseSchedulerService;
import org.thingsboard.server.dao.scheduler.SchedulerDao;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantService;


@Component
public class SchedulerDataValidator extends DataValidator<Scheduler> {

    @Autowired
    private SchedulerDao schedulerDao;

    @Autowired
    @Lazy
    private TenantService tenantService;

    @Override
    protected void validateCreate(TenantId tenantId, Scheduler scheduler) {
        if (!BaseSchedulerService.TB_SERVICE_QUEUE.equals(scheduler.getName())) {
            validateNumberOfEntitiesPerTenant(tenantId, EntityType.SCHEDULER);
        }
    }

    @Override
    protected Scheduler validateUpdate(TenantId tenantId, Scheduler scheduler) {
        Scheduler old = schedulerDao.findById(scheduler.getTenantId(), scheduler.getId().getId());
        if (old == null) {
            throw new DataValidationException("Can't update non existing scheduler!");
        }
        return old;
    }

}
