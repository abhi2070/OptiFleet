package org.thingsboard.server.service.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportColumnType;
import org.thingsboard.server.dao.scheduler.SchedulerService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.scheduler.TbSchedulerService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.sync.ie.importing.csv.AbstractBulkImportService;

import java.util.Map;
import java.util.Optional;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class SchedulerBulkImportService extends AbstractBulkImportService<Scheduler> {
    private final SchedulerService schedulerService;
    private final TbSchedulerService tbSchedulerService;




    @Override
    @SneakyThrows
    protected Scheduler findOrCreateEntity(TenantId tenantId, String name) {
        return Optional.ofNullable(schedulerService.findSchedulerByTenantIdAndName(tenantId, name))
                .orElseGet(Scheduler::new);
    }

    @Override
    protected void setOwners(Scheduler entity, SecurityUser user) {

    }

    @Override
    protected void setEntityFields(Scheduler entity, Map<BulkImportColumnType, String> fields) {

    }

    @Override
    protected Scheduler saveEntity(SecurityUser user, Scheduler entity, Map<BulkImportColumnType, String> fields) {
        return null;
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.SCHEDULER;
    }

}
