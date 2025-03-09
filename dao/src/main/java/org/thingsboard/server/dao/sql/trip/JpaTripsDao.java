package org.thingsboard.server.dao.sql.trip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Trip;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TripId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.TripEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.trip.TripDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;

@Component
@SqlDao
public class JpaTripsDao extends JpaAbstractDao<TripEntity, Trip> implements TripDao {

    @Autowired
    TripRepository tripRepository;

    @Override
    protected Class<TripEntity> getEntityClass() {
        return TripEntity.class;
    }

    @Override
    protected JpaRepository<TripEntity, UUID> getRepository() {
        return tripRepository;
    }

    @Override
    public Trip findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
        return null;
    }

    @Override
    public PageData<Trip> findByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(tripRepository.findByTenantId(tenantId, DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<Trip> findTripsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(tripRepository.findByTenantId(tenantId, pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public TripId getExternalIdByInternal(TripId internalId) {
        return null;
    }

    @Override
    public Long countByTenantId(TenantId tenantId) {
        return tripRepository.countByTenantId(tenantId.getId());
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.TRIP;
    }
}
