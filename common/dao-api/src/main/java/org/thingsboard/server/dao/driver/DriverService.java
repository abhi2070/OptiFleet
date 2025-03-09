package org.thingsboard.server.dao.driver;

import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.common.data.id.DriverId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;

public interface DriverService extends EntityDaoService {

    Driver findDriverById(TenantId tenantId, DriverId driverId);

    Driver saveDriver(Driver driver);

    void deleteDriver(TenantId tenantId, DriverId driverId);

    PageData<Driver> findDriversByTenantId(TenantId tenantId, PageLink pageLink);

}
