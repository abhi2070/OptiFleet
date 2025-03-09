package org.thingsboard.server.service.entity.scheduler;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.scheduler.Scheduler;

public interface TbSchedulerService {

    Scheduler save(Scheduler scheduler, User user) throws Exception;

    void delete(Scheduler scheduler, User user);

    Scheduler update(Scheduler scheduler, User user) throws Exception;

    Scheduler disable(Scheduler scheduler, User user);

}
