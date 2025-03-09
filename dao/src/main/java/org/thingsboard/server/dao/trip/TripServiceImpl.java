package org.thingsboard.server.dao.trip;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Trip;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.HasId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TripId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.Validator;

import java.util.Optional;

@Service("TripDaoService")
@RequiredArgsConstructor
public class TripServiceImpl extends AbstractEntityService implements TripService {

    public static final String INCORRECT_TRIP_ID = "Incorrect tripId ";
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";

    @Autowired
    private TripDao tripDao;

    @Autowired
    private DataValidator<Trip> tripValidator;

    @Override
    public Trip findTripById(TenantId tenantId, TripId tripId) {
        Validator.validateId(tripId, INCORRECT_TRIP_ID + tripId);
        return tripDao.findById(tenantId, tripId.getId());
    }

    @Override
    public Trip saveTrip(Trip trip) {
        return doSaveTrip(trip, true);
    }

    @Override
    public void deleteTrip(TenantId tenantId, TripId tripId) {
        Validator.validateId(tripId, INCORRECT_TRIP_ID + tripId);
        deleteEntityRelations(tenantId, tripId);
        try {
            tripDao.removeById(tenantId, tripId.getId());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public PageData<Trip> findTripsByTenantId(TenantId tenantId, PageLink pageLink) {
        Validator.validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        Validator.validatePageLink(pageLink);
        return tripDao.findTripsByTenantId(tenantId.getId(), pageLink);
    }

    private Trip doSaveTrip(Trip trip, boolean doValidate) {
        if (doValidate) {
            tripValidator.validate(trip, Trip::getTenantId);
        }
        try {
            Trip saved = tripDao.save(trip.getTenantId(), trip);
            return saved;
        } catch (Exception e) {
            checkConstraintViolation(e, "name_unq_key", "Trip with such name already exists!");
            throw e;
        }
    }

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findTripById(tenantId, new TripId(entityId.getId())));
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.TRIP;
    }

}
