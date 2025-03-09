package org.thingsboard.server.dao.sql.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.common.data.id.DriverId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.driver.DriverDao;
import org.thingsboard.server.dao.model.sql.DriverEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;

@Component
@SqlDao
public class JpaDriverDao extends JpaAbstractDao<DriverEntity, Driver> implements DriverDao {

    @Autowired
    DriverRepository driverRepository;

    @Override
    protected Class<DriverEntity> getEntityClass() {
        return DriverEntity.class;
    }

    @Override
    protected JpaRepository<DriverEntity, UUID> getRepository() {
        return driverRepository;
    }

    @Override
    public Driver findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return null;
    }

    @Override
    public PageData<Driver> findByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(driverRepository.findByTenantId(tenantId, DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<Driver> findDriversByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(driverRepository.findByTenantId(tenantId, pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public DriverId getExternalIdByInternal(DriverId internalId) {
        return null;
    }

    @Override
    public Long countByTenantId(TenantId tenantId) {
        return driverRepository.countByTenantId(tenantId.getId());
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DRIVER;
    }

}
