
package org.thingsboard.server.common.transport;

import com.google.protobuf.ByteString;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.id.DeviceProfileId;

public interface TransportDeviceProfileCache {

    DeviceProfile getOrCreate(DeviceProfileId id, ByteString profileBody);

    DeviceProfile get(DeviceProfileId id);

    void put(DeviceProfile profile);

    DeviceProfile put(ByteString profileBody);

    void evict(DeviceProfileId id);

}
