package org.thingsboard.server.dao.vehicle;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.VehicleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.dao.entity.EntityDaoService;

public interface VehicleService extends EntityDaoService {

    Vehicle findVehicleById(TenantId tenantId, VehicleId vehicleId);

    Vehicle saveVehicle(Vehicle vehicle);

    void deleteVehicle(TenantId tenantId, VehicleId vehicleId);

    PageData<Vehicle> findVehicleByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<Vehicle> findVehicleByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);
}
