package org.thingsboard.server.dao.scheduler;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.SchedulerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.common.data.scheduler.SchedulerInfo;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SchedulerDao extends Dao<Scheduler>, TenantEntityDao, ExportableEntityDao<SchedulerId, Scheduler> {


    /**
     * Find schedulers by tenantId and scheduler name.
     *
     * @param tenantId the tenantId
     * @param name the scheduler name
     * @return the optional scheduler object
     */
    Optional<Scheduler> findSchedulersByTenantIdAndName(UUID tenantId, String name);


    /**
     * Find scheduler infos by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of scheduler info objects
     */
    PageData<SchedulerInfo> findSchedulerInfosByTenantId(UUID tenantId, PageLink pageLink);



    /**
     * Find schedulers by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of scheduler objects
     */
    PageData<Scheduler> findSchedulersByTenantId(UUID tenantId, PageLink pageLink);


    /**
     * Save or update scheduler object
     *
     * @param scheduler the scheduler object
     * @return saved scheduler object
     */
    Scheduler save(TenantId tenantId, Scheduler scheduler);

    /**
     * Save or update device object
     *
     * @param scheduler the device object
     * @return saved scheduler object
     */
    Scheduler saveAndFlush(TenantId tenantId, Scheduler scheduler);

    /**
     * Find scheduler by id and tenant id.
     *
     * @param tenantId the tenant id
     * @param id the scheduler id
     * @return the scheduler object
     */
    Scheduler findById(TenantId tenantId, UUID id);


    /**
     * Find scheduler info by id.
     *
     * @param tenantId the tenant id
     * @param schedulerId the scheduler id
     * @return the scheduler info object
     */
    SchedulerInfo findSchedulerInfoById(TenantId tenantId, UUID schedulerId);


    /**
     * Find scheduler by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of scheduler objects
     */
    PageData<Scheduler> findSchedulerByTenantId(UUID tenantId, PageLink pageLink);
}






