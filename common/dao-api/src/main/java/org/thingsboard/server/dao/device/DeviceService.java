
package org.thingsboard.server.dao.device;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceIdInfo;
import org.thingsboard.server.common.data.DeviceInfo;
import org.thingsboard.server.common.data.DeviceInfoFilter;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.device.DeviceSearchQuery;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.ota.OtaPackageType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.dao.device.provision.ProvisionRequest;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.UUID;

public interface DeviceService extends EntityDaoService {

    DeviceInfo findDeviceInfoById(TenantId tenantId, DeviceId deviceId);

    DeviceInfo findDeviceInfById(DeviceId deviceId);

    List<String> findAllIdByDeviceType(String deviceType);

    Device findDeviceById(TenantId tenantId, DeviceId deviceId);

    ListenableFuture<Device> findDeviceByIdAsync(TenantId tenantId, DeviceId deviceId);

    Device findDeviceByTenantIdAndName(TenantId tenantId, String name);

    Device saveDevice(Device device, boolean doValidate);

    Device saveDevice(Device device);

    Device saveDeviceWithAccessToken(Device device, String accessToken);

    Device saveDeviceWithCredentials(Device device, DeviceCredentials deviceCredentials);

    Device saveDevice(ProvisionRequest provisionRequest, DeviceProfile profile);

    Device assignDeviceToCustomer(TenantId tenantId, DeviceId deviceId, CustomerId customerId);

    Device unassignDeviceFromCustomer(TenantId tenantId, DeviceId deviceId);

    void deleteDevice(TenantId tenantId, DeviceId deviceId);

    PageData<Device> findDevicesByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<DeviceInfo> findDeviceInfosByFilter(DeviceInfoFilter filter, PageLink pageLink);

    PageData<DeviceIdInfo> findDeviceIdInfos(PageLink pageLink);

    PageData<Device> findDevicesByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    PageData<Device> findDevicesByTenantIdAndTypeAndEmptyOtaPackage(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType type, PageLink pageLink);

    Long countDevicesByTenantIdAndDeviceProfileIdAndEmptyOtaPackage(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType otaPackageType);

    ListenableFuture<List<Device>> findDevicesByTenantIdAndIdsAsync(TenantId tenantId, List<DeviceId> deviceIds);

    List<Device> findDevicesByIds(List<DeviceId> deviceIds);

    ListenableFuture<List<Device>> findDevicesByIdsAsync(List<DeviceId> deviceIds);

    void deleteDevicesByTenantId(TenantId tenantId);

    PageData<Device> findDevicesByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    PageData<Device> findDevicesByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    ListenableFuture<List<Device>> findDevicesByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<DeviceId> deviceIds);

    void unassignCustomerDevices(TenantId tenantId, CustomerId customerId);

    ListenableFuture<List<Device>> findDevicesByQuery(TenantId tenantId, DeviceSearchQuery query);

//    @Deprecated(since = "3.6.2", forRemoval = true)
    ListenableFuture<List<EntitySubtype>> findDeviceTypesByTenantId(TenantId tenantId);

    Device assignDeviceToTenant(TenantId tenantId, Device device);

    PageData<UUID> findDevicesIdsByDeviceProfileTransportType(DeviceTransportType transportType, PageLink pageLink);

    Device assignDeviceToEdge(TenantId tenantId, DeviceId deviceId, EdgeId edgeId);

    Device unassignDeviceFromEdge(TenantId tenantId, DeviceId deviceId, EdgeId edgeId);

    PageData<Device> findDevicesByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink);

    PageData<Device> findDevicesByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink);

}
