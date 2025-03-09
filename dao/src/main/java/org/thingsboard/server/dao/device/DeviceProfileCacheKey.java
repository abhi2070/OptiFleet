
package org.thingsboard.server.dao.device;

import lombok.Data;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;

import java.io.Serializable;

@Data
public class DeviceProfileCacheKey implements Serializable {

    private static final long serialVersionUID = 8220455917177676472L;

    private final TenantId tenantId;
    private final String name;
    private final DeviceProfileId deviceProfileId;
    private final boolean defaultProfile;
    private final String provisionDeviceKey;

    private DeviceProfileCacheKey(TenantId tenantId, String name, DeviceProfileId deviceProfileId, boolean defaultProfile, String provisionDeviceKey) {
        this.tenantId = tenantId;
        this.name = name;
        this.deviceProfileId = deviceProfileId;
        this.defaultProfile = defaultProfile;
        this.provisionDeviceKey = provisionDeviceKey;
    }

    public static DeviceProfileCacheKey fromName(TenantId tenantId, String name) {
        return new DeviceProfileCacheKey(tenantId, name, null, false, null);
    }

    public static DeviceProfileCacheKey fromId(DeviceProfileId id) {
        return new DeviceProfileCacheKey(null, null, id, false, null);
    }

    public static DeviceProfileCacheKey defaultProfile(TenantId tenantId) {
        return new DeviceProfileCacheKey(tenantId, null, null, true, null);
    }

    public static DeviceProfileCacheKey fromProvisionDeviceKey(String provisionDeviceKey) {
        return new DeviceProfileCacheKey(null, null, null, false, provisionDeviceKey);
    }

    /**
     * IMPORTANT: Method toString() has to return unique value, if you add additional field to this class, please also refactor toString().
     */
    @Override
    public String toString() {
        if (deviceProfileId != null) {
            return deviceProfileId.toString();
        } else if (defaultProfile) {
            return tenantId.toString();
        } else if (StringUtils.isNotEmpty(provisionDeviceKey)) {
            return provisionDeviceKey;
        }
        return tenantId + "_" + name;
    }
}
