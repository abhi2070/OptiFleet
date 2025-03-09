package org.thingsboard.server.dao.scheduler;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.roles.RolesInfo;
import org.thingsboard.server.common.data.roles.RolesSearchQuery;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.common.data.scheduler.SchedulerInfo;
import org.thingsboard.server.dao.entity.EntityDaoService;
import org.thingsboard.server.common.data.scheduler.SchedulerSearchQuery;

import java.util.List;

public interface SchedulerService extends EntityDaoService {

    Scheduler saveScheduler(Scheduler scheduler);

    PageData<SchedulerInfo> findSchedulerInfosByTenantId(TenantId tenantId, PageLink pageLink);

    Scheduler findSchedulerById(TenantId tenantId, SchedulerId schedulerId);

    Scheduler findSchedulerByTenantIdAndName(TenantId tenantId, String name);

    Scheduler savescheduler(Scheduler scheduler, boolean doValidate);

    ListenableFuture<Scheduler> findSchedulerByIdAsync(TenantId tenantId, SchedulerId schedulerId);

    SchedulerInfo findSchedulerInfoById(TenantId tenantId, SchedulerId schedulerId);

    ListenableFuture<List<Scheduler>> findSchedulerByQuery(TenantId tenantId, SchedulerSearchQuery query);

    void deleteScheduler(TenantId tenantId, SchedulerId schedulerId);

    void deleteSchedulersByTenantId(TenantId tenantId);

    PageData<Scheduler> findSchedulersByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<Scheduler> findSchedulerByTenantId(TenantId tenantId, PageLink pageLink);
}
