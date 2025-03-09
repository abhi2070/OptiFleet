package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.VehicleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.vehicle.TbVehicleService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import static org.thingsboard.server.controller.ControllerConstants.*;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class VehicleController extends BaseController{

    private final TbVehicleService tbVehicleService;

    public static final String VEHICLE_ID = "vehicleId";

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/vehicle/{vehicleId}", method = RequestMethod.GET)
    @ResponseBody
    public Vehicle getVehicleById(
            @ApiParam(value = VEHICLE_ID_PARAM_DESCRIPTION)
            @PathVariable(VEHICLE_ID) String strVehicleId) throws ThingsboardException {
        checkParameter(VEHICLE_ID, strVehicleId);
        VehicleId vehicleId = new VehicleId(toUUID(strVehicleId));
        return checkVehicleId(vehicleId, Operation.READ);
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/vehicle", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Vehicle> getTenantVehicle(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION)
            @RequestParam int page,
            @ApiParam(value = VEHICLE_TYPE_DESCRIPTION)
            @RequestParam(required = false) String type,
            @ApiParam(value = VEHICLE_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = VEHICLE_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        if (type != null && type.trim().length() > 0) {
            return checkNotNull(vehicleService.findVehicleByTenantIdAndType(tenantId, type, pageLink));
        }
        return checkNotNull(vehicleService.findVehicleByTenantId(tenantId, pageLink));
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/vehicle", method = RequestMethod.POST)
    @ResponseBody
    public Vehicle saveVehicle(
            @ApiParam(value = "A JSON value representing the vehicle.")
            @RequestBody Vehicle vehicle) throws Exception {
        vehicle.setTenantId(getTenantId());
        checkEntity(vehicle.getId(), vehicle, Resource.VEHICLE);
        return tbVehicleService.save(vehicle, getCurrentUser());
    }


    @ApiOperation(value = "Update Vehicle Documents", notes = "Update documents for a vehicle.")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/vehicle/documents/update/{vehicleId}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Vehicle updateVehicleDocuments(
            @PathVariable(VEHICLE_ID) String strVehicleId,
            @RequestParam(value ="registrationCertificate", required = false) MultipartFile registrationCertificate,
            @RequestParam(value = "insuranceCertificate", required = false) MultipartFile insuranceCertificate,
            @RequestParam(value = "pucCertificate", required = false) MultipartFile pucCertificate,
            @RequestParam(value = "requiredPermits", required = false) MultipartFile requiredPermits) throws Exception {
        checkParameter(VEHICLE_ID, strVehicleId);
        VehicleId vehicleId = new VehicleId(toUUID(strVehicleId));
        checkVehicleId(vehicleId, Operation.WRITE);
        Vehicle vehicle = checkVehicleId(vehicleId, Operation.READ);
        return tbVehicleService.updateDocument(vehicle, getCurrentUser(), registrationCertificate, insuranceCertificate,
                pucCertificate, requiredPermits);
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/vehicle/{vehicleId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteVehicle(
            @ApiParam(value = VEHICLE_ID_PARAM_DESCRIPTION)
            @PathVariable(VEHICLE_ID) String strVehicleId) throws Exception {
        checkParameter(VEHICLE_ID, strVehicleId);
        VehicleId vehicleId = new VehicleId(toUUID(strVehicleId));
        Vehicle vehicle = checkVehicleId(vehicleId, Operation.DELETE);
        tbVehicleService.delete(vehicle, getCurrentUser());
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/vehicle/{vehicleId}/verify", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public boolean vehicleVerification(
            @ApiParam(value = "")
            @PathVariable(VEHICLE_ID) String strVehicleId) throws Exception {
        checkParameter(VEHICLE_ID, strVehicleId);
        VehicleId vehicleId = new VehicleId(toUUID(strVehicleId));
        Vehicle vehicle = checkVehicleId(vehicleId, Operation.READ);
        return tbVehicleService.isVerifiedVehicle(vehicle, getCurrentUser());
    }
}
