package org.thingsboard.server.service.entity.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.SchedulerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.dao.scheduler.SchedulerService;
import org.thingsboard.server.dao.sql.scheduler.SchedulerRepository;
import org.thingsboard.server.service.entity.AbstractTbEntityService;

import static org.thingsboard.server.dao.scheduler.BaseSchedulerService.TB_SERVICE_QUEUE;

@Service
@Slf4j
@AllArgsConstructor
public class DefaultTbSchedulerService extends AbstractTbEntityService implements TbSchedulerService {

    private final SchedulerService schedulerService;
    private final SchedulerRepository schedulerRepository;
    public static final String UTF_8 = "UTF-8";

    @Override
    @Transactional
    public Scheduler save(Scheduler scheduler, User user) throws Exception {
        ActionType actionType = scheduler.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = scheduler.getTenantId();

        try {
            if (TB_SERVICE_QUEUE.equals(scheduler.getName())) {
                throw new ThingsboardException("Unable to save scheduler with name " + TB_SERVICE_QUEUE, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            Scheduler savedScheduler = checkNotNull(schedulerService.saveScheduler(scheduler));
            autoCommit(user, savedScheduler.getId());
            return savedScheduler;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.SCHEDULER), scheduler, actionType, user, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Scheduler scheduler, User user) {
        ActionType actionType = ActionType.DELETED;
        TenantId tenantId = scheduler.getTenantId();
        SchedulerId schedulerId = scheduler.getId();
        try {
            log.info("Attempting to delete scheduler: {}", schedulerId);
            schedulerService.deleteScheduler(tenantId, schedulerId);
        } catch (Exception e) {
            log.error("Error occurred while deleting scheduler: {}", schedulerId, e);
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.SCHEDULER), actionType, user, e, schedulerId.toString());
            throw e;
        }
    }


    @Override
    public Scheduler update(Scheduler scheduler, User user) throws Exception {
        return null;
    }

    @Override
    public Scheduler disable(Scheduler scheduler, User user) {
        return null;
    }

}
