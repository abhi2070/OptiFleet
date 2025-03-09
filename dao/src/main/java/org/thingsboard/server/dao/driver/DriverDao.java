package org.thingsboard.server.dao.driver;

import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.common.data.id.DriverId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.UUID;

public interface DriverDao extends Dao<Driver>, TenantEntityDao, ExportableEntityDao<DriverId, Driver> {

    /**
     * Save or update driver object
     *
     * @param driver the driver object
     * @return saved driver object
     */
    Driver save(TenantId tenantId, Driver driver);

    /**
     * Find drivers by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of driver objects
     */
    PageData<Driver> findDriversByTenantId(UUID tenantId, PageLink pageLink);

}
