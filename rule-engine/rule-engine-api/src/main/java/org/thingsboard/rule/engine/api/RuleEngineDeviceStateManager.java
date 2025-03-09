
package org.thingsboard.rule.engine.api;

import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.TbCallback;

public interface RuleEngineDeviceStateManager {

    void onDeviceConnect(TenantId tenantId, DeviceId deviceId, long connectTime, TbCallback callback);

    void onDeviceActivity(TenantId tenantId, DeviceId deviceId, long activityTime, TbCallback callback);

    void onDeviceDisconnect(TenantId tenantId, DeviceId deviceId, long disconnectTime, TbCallback callback);

    void onDeviceInactivity(TenantId tenantId, DeviceId deviceId, long inactivityTime, TbCallback callback);

}
