package org.thingsboard.server.common.data.scheduler;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.SchedulerId;

@ApiModel(description = "Scheduler information details")
@Data
@EqualsAndHashCode(callSuper = true)
public class SchedulerInfo extends Scheduler{

    private static final long serialVersionUID = -4094528227011066194L;

    public SchedulerInfo() {
        super();
    }

    public SchedulerInfo(SchedulerId schedulerId) {
        super(schedulerId);
    }

    public SchedulerInfo(Scheduler scheduler) {
        super(scheduler);

    }


}