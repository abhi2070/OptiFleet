package org.thingsboard.server.dao.scheduler;


import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.common.data.scheduler.SchedulerInfo;
import org.thingsboard.server.common.data.scheduler.SchedulerSearchQuery;
import org.thingsboard.server.dao.entity.AbstractCachedEntityService;
import org.springframework.stereotype.Service;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.dao.entity.EntityCountService;
import org.thingsboard.server.dao.entityview.EntityViewService;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;
import org.thingsboard.server.dao.sql.scheduler.SchedulerRepository;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.service.Validator.*;

@Service("SchedulerDaoService")
@Slf4j
public class BaseSchedulerService extends AbstractCachedEntityService<SchedulerCacheKey, Scheduler, SchedulerCacheEvictEvent> implements SchedulerService {
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_SCHEDULER_ID = "Incorrect schedulerId ";
    public static final String TB_SERVICE_QUEUE = "TbServiceQueue";
    public static final String DUPLICATE_MESSAGE = "such Email already exists!";



    @Autowired
    private SchedulerDao schedulerDao;

    private long timeout;

    @Autowired
    private EntityCountService countService;

    @Autowired
    private DataValidator<Scheduler> schedulerValidator;

    @Autowired
    private SchedulerRepository schedulerRepository;
    @Lazy
    @Autowired
    protected EntityViewService entityViewService;

    @Override
    public Scheduler saveScheduler(Scheduler scheduler) {
        return doSaveScheduler(scheduler,true);
    }


    @Override
    public PageData<SchedulerInfo> findSchedulerInfosByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findSchedulerInfosByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return schedulerDao.findSchedulerInfosByTenantId(tenantId.getId(), pageLink);
    }



    private Scheduler doSaveScheduler(Scheduler scheduler, boolean doValidate) {
        log.trace("Executing saveScheduler[{}]", scheduler);
        Scheduler oldScheduler = null;
        if (doValidate) {
            oldScheduler = schedulerValidator.validate(scheduler, Scheduler::getTenantId);
        } else if (scheduler.getId() != null) {
            oldScheduler = findSchedulerById(scheduler.getTenantId(), scheduler.getId());
        }
        SchedulerCacheEvictEvent evictEvent = new SchedulerCacheEvictEvent(scheduler.getTenantId(), scheduler.getName(), oldScheduler != null ? oldScheduler.getName() : null);
        Scheduler savedScheduler;
        try {
            savedScheduler = schedulerDao.saveAndFlush(scheduler.getTenantId(), scheduler);
            publishEvictEvent(evictEvent);
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(savedScheduler.getTenantId())
                    .entityId(savedScheduler.getId()).created(scheduler.getId() == null).build());
            if (scheduler.getId() == null) {
                countService.publishCountEntityEvictEvent(savedScheduler.getTenantId(), EntityType.SCHEDULER);
            }
        } catch (Exception t) {
            handleEvictEvent(evictEvent);
            checkConstraintViolation(t,
                    "scheduler_name_unq_key", "Scheduler with such name already exists!",
                    "scheduler_external_id_unq_key", "Scheduler with such external id already exists!");  // Added external ID check
            throw t;
        }
        return savedScheduler;
    }

    @Override
    public Scheduler findSchedulerById(TenantId tenantId, SchedulerId schedulerId) {
        log.trace("Executing findSchedulerById [{}]", schedulerId);
        validateId(schedulerId, INCORRECT_SCHEDULER_ID + schedulerId);
        return schedulerDao.findById(tenantId, schedulerId.getId());

    }

    @Override
    public Scheduler findSchedulerByTenantIdAndName(TenantId tenantId, String name) {
        log.trace("Executing findSchedulerByTenantIdAndName [{}][{}]", tenantId, name);
        validateId(tenantId, INCORRECT_TENANT_ID+tenantId);
        return cache.getAndPutInTransaction(new SchedulerCacheKey(tenantId, name),
                ()-> schedulerDao.findSchedulersByTenantIdAndName(tenantId.getId(), name)
                        .orElse(null), true);
    }


    @Override
    public Scheduler savescheduler(Scheduler scheduler, boolean doValidate)
    {
        return doSaveScheduler(scheduler, doValidate);
    }

    @Override
    public ListenableFuture<Scheduler> findSchedulerByIdAsync(TenantId tenantId, SchedulerId schedulerId) {
        log.trace("Executing findSchedulerById [{}]", schedulerId);
        validateId(schedulerId, INCORRECT_SCHEDULER_ID + schedulerId);
        return schedulerDao.findByIdAsync(tenantId, schedulerId.getId());
    }

    @Override
    public SchedulerInfo findSchedulerInfoById(TenantId tenantId, SchedulerId schedulerId) {
        log.trace("Executing findSchedulerInfoById [{}]", schedulerId);
        validateId(schedulerId, INCORRECT_SCHEDULER_ID + schedulerId);
        return schedulerDao.findSchedulerInfoById(tenantId, schedulerId.getId());
    }

//    @Override
//    public PageData<Scheduler> findSchedulersByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink) {
//        log.trace("Executing findSchedulersByTenantIdAndType, tenantId [{}], type [{}], pageLink [{}]", tenantId, type, pageLink);
//        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
//        validateString(type, "Incorrect type " + type);
//        validatePageLink(pageLink);
//        return schedulerDao.findSchedulersByTenantIdAndType(tenantId.getId(), type, pageLink);
//    }


    @Override
    public void deleteScheduler(TenantId tenantId, SchedulerId schedulerId) {
        validateId(schedulerId, INCORRECT_SCHEDULER_ID + schedulerId);
        if (entityViewService.existsByTenantIdAndEntityId(tenantId, schedulerId)) {
            throw new DataValidationException("Can't delete scheduler that has entity views!");
        }

        Scheduler scheduler = schedulerDao.findById(tenantId, schedulerId.getId());
        deleteScheduler(tenantId, scheduler);

    }

    @Override
    public void deleteSchedulersByTenantId(TenantId tenantId) {
        log.trace("Executing deleteSchedulersByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        tenantSchedulersRemover.removeEntities(tenantId, tenantId);

    }

    private void deleteScheduler(TenantId tenantId, Scheduler scheduler) {
        log.trace("Executing deleteScheduler [{}]", scheduler.getId());
        relationService.deleteEntityRelations(tenantId, scheduler.getId());

        schedulerDao.removeById(tenantId, scheduler.getUuidId());

        publishEvictEvent(new SchedulerCacheEvictEvent(scheduler.getTenantId(), scheduler.getName(), null));
        countService.publishCountEntityEvictEvent(tenantId, EntityType.SCHEDULER);
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(scheduler.getId()).build());
    }

    @Override
    public PageData<Scheduler> findSchedulersByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findSchedulersByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_SCHEDULER_ID + tenantId);
        validatePageLink(pageLink);
        return schedulerDao.findSchedulersByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public PageData<Scheduler> findSchedulerByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findSchedulerByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return schedulerDao.findSchedulerByTenantId(tenantId.getId(), pageLink);
    }


    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.empty();
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.SCHEDULER;
    }


    @Override
    public void handleEvictEvent(SchedulerCacheEvictEvent event) {
        List<SchedulerCacheKey> keys = new ArrayList<>(2);
        keys.add(new SchedulerCacheKey(event.getTenantId(), event.getNewName()));
        if (StringUtils.isNotEmpty(event.getOldName()) && !event.getOldName().equals(event.getNewName())) {
            keys.add(new SchedulerCacheKey(event.getTenantId(), event.getOldName()));
        }
        cache.evict(keys);
    }



    @Override
    public ListenableFuture<List<Scheduler>> findSchedulerByQuery(TenantId tenantId, SchedulerSearchQuery query) {
        ListenableFuture<List<EntityRelation>> relations = relationService.findByQuery(tenantId, query.toEntitySearchQuery());
        ListenableFuture<List<Scheduler>> scheduler = Futures.transformAsync(relations, r -> {
            EntitySearchDirection direction = query.toEntitySearchQuery().getParameters().getDirection();
            List<ListenableFuture<Scheduler>> futures = new ArrayList<>();
            for (EntityRelation relation : r) {
                EntityId entityId = direction == EntitySearchDirection.FROM ? relation.getTo() : relation.getFrom();
                if (entityId.getEntityType() == EntityType.SCHEDULER) {
                    futures.add(findSchedulerByIdAsync(tenantId, new SchedulerId(entityId.getId())));
                }
            }
            return Futures.successfulAsList(futures);
        }, MoreExecutors.directExecutor());
        scheduler = Futures.transform(scheduler, schedulerList ->
                        schedulerList == null ?
                                Collections.emptyList() :
                                schedulerList.stream()
                                        .collect(Collectors.toList()),
                MoreExecutors.directExecutor()
        );
        return scheduler;
    }


    @Override
    @Transactional
    public void deleteEntity(TenantId tenantId, EntityId id) {
        deleteScheduler(tenantId, (SchedulerId) id);
    }



    private final PaginatedRemover<TenantId, Scheduler> tenantSchedulersRemover = new PaginatedRemover<>() {

        @Override
        protected PageData<Scheduler> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
            return schedulerDao.findSchedulersByTenantId(id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, Scheduler scheduler) {
            deleteScheduler(tenantId, scheduler);
        }
    };
}
