package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.Trip;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TripId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.trip.TbTripService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import static org.thingsboard.server.controller.ControllerConstants.*;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
public class TripController extends BaseController {

    private final TbTripService tbTripService;

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/trip/info/{tripId}", method = RequestMethod.GET)
    @ResponseBody
    public Trip getTripInfoById(
            @ApiParam(value = "")
            @PathVariable(TRIP_ID) String strTripId) throws ThingsboardException {
        checkParameter(TRIP_ID, strTripId);
        TripId tripId = new TripId(toUUID(strTripId));
        return checkTripId(tripId, Operation.READ);
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/trip", method = RequestMethod.POST)
    @ResponseBody
    public Trip saveTrip(
            @ApiParam(value = "")
            @RequestBody Trip trip) throws Exception {
        trip.setTenantId(getTenantId());
        checkEntity(trip.getId(), trip, Resource.TRIP);
        return tbTripService.save(trip, getCurrentUser());
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/trips", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Trip> getTenantTrips(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = TRIP_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = TRIP_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder)throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return checkNotNull(tripService.findTripsByTenantId(tenantId, pageLink));
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/trip/{tripId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteTrip(
            @ApiParam(value = "")
            @PathVariable(TRIP_ID) String strTripId) throws Exception {
        checkParameter(TRIP_ID, strTripId);
        TripId tripId = new TripId(toUUID(strTripId));
        Trip trip = checkTripId(tripId, Operation.DELETE);
        tbTripService.deleteTrip(trip, getCurrentUser());
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/trip/startOrStop/{tripId}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public Trip startOrStopTrip(
            @ApiParam(value = "")
            @PathVariable(TRIP_ID) String strTripId) throws Exception {
        checkParameter(TRIP_ID, strTripId);
        TripId tripId = new TripId(toUUID(strTripId));
        Trip trip = new Trip();
        trip.setId(tripId);
        trip.setTenantId(getTenantId());
        Trip existingTrip = checkTripId(tripId, Operation.WRITE);
        return tbTripService.startOrStopTrip(existingTrip, trip, getCurrentUser());
    }

}
