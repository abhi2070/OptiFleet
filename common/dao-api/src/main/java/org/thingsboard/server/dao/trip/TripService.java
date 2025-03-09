package org.thingsboard.server.dao.trip;

import org.thingsboard.server.common.data.Trip;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TripId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

public interface TripService extends EntityDaoService {

    Trip findTripById(TenantId tenantId, TripId tripId);

    Trip saveTrip(Trip trip);

    void deleteTrip(TenantId tenantId, TripId tripId);

    PageData<Trip> findTripsByTenantId(TenantId tenantId, PageLink pageLink);

}
