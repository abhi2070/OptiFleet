package org.thingsboard.server.dao.sql.vehicle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.VehicleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.VehicleEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;
import org.thingsboard.server.dao.vehicle.VehicleDao;

import java.util.Optional;
import java.util.UUID;

import static org.thingsboard.server.dao.vehicle.BaseVehicleService.TB_SERVICE_QUEUE;

@Component
@SqlDao
@Slf4j
public class JpaVehicleDao extends JpaAbstractDao<VehicleEntity, Vehicle> implements VehicleDao {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    protected Class<VehicleEntity> getEntityClass() {
        return VehicleEntity.class;
    }

    @Override
    protected JpaRepository<VehicleEntity, UUID> getRepository() {
        return vehicleRepository;
    }

    @Override
    public PageData<Vehicle> findVehicleByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(vehicleRepository.findByTenantId(tenantId, pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public Long countByTenantId(TenantId tenantId) {
        return vehicleRepository.countByTenantIdAndTypeIsNot(tenantId.getId(), TB_SERVICE_QUEUE);
    }

    @Override
    public Vehicle findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return null;
    }

    @Override
    public PageData<Vehicle> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findVehicleByTenantId(tenantId, pageLink);
    }

    @Override
    public PageData<Vehicle> findVehicleByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(vehicleRepository.findByTenantIdAndType( tenantId,type,pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public VehicleId getExternalIdByInternal(VehicleId internalId) {
        return null;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.VEHICLE;
    }

}
