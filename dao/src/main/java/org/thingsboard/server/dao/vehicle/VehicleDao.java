package org.thingsboard.server.dao.vehicle;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.VehicleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.UUID;

public interface VehicleDao extends Dao<Vehicle>, TenantEntityDao, ExportableEntityDao<VehicleId, Vehicle> {

    Vehicle save(TenantId tenantId, Vehicle vehicle);

    PageData<Vehicle> findVehicleByTenantId(UUID tenantId, PageLink pageLink);

    PageData<Vehicle> findVehicleByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);
}
