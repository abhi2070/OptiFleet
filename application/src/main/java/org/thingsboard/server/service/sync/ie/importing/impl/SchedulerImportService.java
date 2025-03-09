package org.thingsboard.server.service.sync.ie.importing.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.SchedulerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.dao.scheduler.SchedulerService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class SchedulerImportService extends BaseEntityImportService<SchedulerId, Scheduler, EntityExportData<Scheduler>>{


    private final SchedulerService schedulerService;

    @Override
    public EntityType getEntityType() {
        return  EntityType.SCHEDULER;
    }

    @Override
    protected void setOwner(TenantId tenantId,Scheduler scheduler ,IdProvider idProvider) {
        scheduler.setTenantId((tenantId));

    }

    @Override
    protected Scheduler prepare(EntitiesImportCtx ctx, Scheduler scheduler, Scheduler old, EntityExportData<Scheduler> exportData, IdProvider idProvider) {
        return scheduler ;
    }

    @Override
    protected Scheduler deepCopy(Scheduler scheduler) {
       return  new Scheduler(scheduler);
    }

    @Override
    protected Scheduler saveOrUpdate(EntitiesImportCtx ctx, Scheduler scheduler, EntityExportData<Scheduler> exportData, IdProvider idProvider) {
        return  schedulerService.saveScheduler(scheduler);
    }
    @Override
    protected void cleanupForComparison(Scheduler e) {
        super.cleanupForComparison(e);
    }
}
