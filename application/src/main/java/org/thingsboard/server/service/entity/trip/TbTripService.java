package org.thingsboard.server.service.entity.trip;

import org.thingsboard.server.common.data.Trip;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.service.entity.SimpleTbEntityService;

public interface TbTripService extends SimpleTbEntityService<Trip> {

    Trip startOrStopTrip(Trip existingTrip, Trip trip, User user) throws Exception;

    void deleteTrip(Trip trip, User user) throws Exception;

}
