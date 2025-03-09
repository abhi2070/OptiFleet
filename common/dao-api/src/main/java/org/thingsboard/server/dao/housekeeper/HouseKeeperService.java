
package org.thingsboard.server.dao.housekeeper;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.List;

public interface HouseKeeperService {

    ListenableFuture<List<AlarmId>> unassignDeletedUserAlarms(TenantId tenantId, User user, long unassignTs);

}
