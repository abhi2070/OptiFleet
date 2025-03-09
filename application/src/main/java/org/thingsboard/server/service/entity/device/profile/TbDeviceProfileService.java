
package org.thingsboard.server.service.entity.device.profile;

import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.service.entity.SimpleTbEntityService;

public interface TbDeviceProfileService extends SimpleTbEntityService<DeviceProfile> {

    DeviceProfile setDefaultDeviceProfile(DeviceProfile deviceProfile, DeviceProfile previousDefaultDeviceProfile, User user) throws ThingsboardException;
}
