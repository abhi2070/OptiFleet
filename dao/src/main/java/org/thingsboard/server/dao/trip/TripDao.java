package org.thingsboard.server.dao.trip;

import org.thingsboard.server.common.data.Trip;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TripId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.UUID;

public interface TripDao extends Dao<Trip>, TenantEntityDao, ExportableEntityDao<TripId, Trip> {

    /**
     * Save or update trip object
     *
     * @param trip the trip object
     * @return saved trip object
     */
    Trip save(TenantId tenantId, Trip trip);

    /**
     * Find trips by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of trip objects
     */
    PageData<Trip> findTripsByTenantId(UUID tenantId, PageLink pageLink);

}
