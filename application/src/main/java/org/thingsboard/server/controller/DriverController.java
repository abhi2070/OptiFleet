package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DriverId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.driver.TbDriverService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;

import static org.thingsboard.server.controller.ControllerConstants.*;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_ALLOWABLE_VALUES;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
public class DriverController extends BaseController {

    private final TbDriverService tbDriverService;

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/driver/info/{driverId}", method = RequestMethod.GET)
    @ResponseBody
    public Driver getDriverInfoById(
            @ApiParam(value = "")
            @PathVariable(DRIVER_ID) String strDriverId) throws ThingsboardException {
        checkParameter(DRIVER_ID, strDriverId);
        DriverId driverId = new DriverId(toUUID(strDriverId));
        return checkDriverId(driverId, Operation.READ);
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/driver", method = RequestMethod.POST)
    @ResponseBody
    public Driver saveDriver(
            @ApiParam(value = "")
            @RequestBody Driver driver) throws Exception {
        driver.setTenantId(getTenantId());
        checkEntity(driver.getId(), driver, Resource.DRIVER);
        return tbDriverService.save(driver, getCurrentUser());
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/drivers", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<Driver> getTenantDrivers(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = DRIVER_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = DRIVER_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return checkNotNull(driverService.findDriversByTenantId(tenantId, pageLink));
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/driver/{driverId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteDriver(
            @ApiParam(value = "")
            @PathVariable(DRIVER_ID) String strDriverId) throws Exception {
        checkParameter(DRIVER_ID, strDriverId);
        DriverId driverId = new DriverId(toUUID(strDriverId));
        Driver driver = checkDriverId(driverId, Operation.DELETE);
        tbDriverService.delete(driver, getCurrentUser());
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/driver/{driverId}/verify", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public boolean driverVerification(
            @ApiParam(value = "")
            @PathVariable(DRIVER_ID) String strDriverId) throws Exception {
        checkParameter(DRIVER_ID, strDriverId);
        DriverId driverId = new DriverId(toUUID(strDriverId));
        Driver driver = checkDriverId(driverId, Operation.READ);
        return tbDriverService.isVerifiedDriver(driver, getCurrentUser());
    }

    @ApiOperation(value = "", notes = "")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/driver/document/{driverId}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public Driver uploadDriverPhotoDocument(
            @ApiParam(value = "")
            @PathVariable(DRIVER_ID) String strDriverId,
            @ApiParam(value = "")
            @RequestPart("file") MultipartFile file) throws Exception {
        checkParameter(DRIVER_ID, strDriverId);
        DriverId driverId = new DriverId(toUUID(strDriverId));
        Driver driver = checkDriverId(driverId, Operation.READ);
        return tbDriverService.uploadPhotoDocument(file.getBytes(), file.getContentType(), driver, getCurrentUser());
    }

}
