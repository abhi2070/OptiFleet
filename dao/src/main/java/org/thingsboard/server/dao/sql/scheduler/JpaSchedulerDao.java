package org.thingsboard.server.dao.sql.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.SchedulerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.common.data.scheduler.SchedulerInfo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.AssetInfoEntity;
import org.thingsboard.server.dao.model.sql.RolesInfoEntity;
import org.thingsboard.server.dao.model.sql.SchedulerEntity;
import org.thingsboard.server.dao.model.sql.SchedulerInfoEntity;
import org.thingsboard.server.dao.scheduler.SchedulerDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.thingsboard.server.dao.scheduler.BaseSchedulerService.TB_SERVICE_QUEUE;

@Slf4j
@Component
@SqlDao
public class JpaSchedulerDao extends JpaAbstractDao<SchedulerEntity,Scheduler> implements SchedulerDao {

    @Autowired
    private SchedulerRepository schedulerRepository;



    @Override
    @Transactional
    public Scheduler saveAndFlush(TenantId tenantId, Scheduler scheduler) {
        Scheduler result = save(tenantId, scheduler);
        schedulerRepository.flush();
        return result;
    }

    @Override
    public Optional<Scheduler> findSchedulersByTenantIdAndName(UUID tenantId, String name) {
        Scheduler scheduler = DaoUtil.getData(schedulerRepository.findByTenantIdAndName(tenantId, name));
        return Optional.ofNullable(scheduler);
    }

    @Override
    public PageData<SchedulerInfo> findSchedulerInfosByTenantId(UUID tenantId, PageLink pageLink) {
        log.info("working");
//        log.info(String.valueOf(schedulerRepository.findSchedulerInfosByTenantIdTest(tenantId)).toString());
        return DaoUtil.toPageData(
                schedulerRepository.findSchedulerInfosByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink, SchedulerInfoEntity.schedulerInfoColumnMap)));
    }

//    @Override
//    public PageData<SchedulerInfo> findSchedulerInfosByTenantId(UUID tenantId, PageLink pageLink) {
//        return DaoUtil.toPageData(
//               schedulerRepository.findSchedulerInfosByTenantId(
//                        tenantId,
//                        pageLink.getTextSearch(),
//                        DaoUtil.toPageable(pageLink, SchedulerInfoEntity.schedulerInfoColumnMap)));
//    }

    @Override
    public PageData<Scheduler> findSchedulersByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(schedulerRepository
                .findByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public SchedulerInfo findSchedulerInfoById(TenantId tenantId, UUID schedulerId) {
        return DaoUtil.getData(schedulerRepository.findSchedulerInfoById(schedulerId));
    }

    @Override
    public PageData<Scheduler> findSchedulerByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(schedulerRepository
                .findByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }


    @Override
    protected Class<SchedulerEntity> getEntityClass() {
        return SchedulerEntity.class;
    }

    @Override
    protected JpaRepository<SchedulerEntity, UUID> getRepository() {
        return schedulerRepository;
    }


    @Override
    public Scheduler findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
//        return DaoUtil.getData(schedulerRepository.findByTenantIdAndExternalId(tenantId, externalId));
        return null;
    }


    @Override
    public PageData<Scheduler> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findSchedulersByTenantId(tenantId, pageLink);
    }

    @Override
    public SchedulerId getExternalIdByInternal(SchedulerId internalId) {
        return Optional.ofNullable(schedulerRepository.getExternalIdById(internalId.getId()))
                .map(SchedulerId::new).orElse(null);
    }



    @Override
    public Long countByTenantId(TenantId tenantId) {
        return 0L;
    }
}
