package org.thingsboard.server.dao.model.sql;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.dao.util.mapping.JsonStringType;
import javax.persistence.Entity;
import javax.persistence.Table;
import static org.thingsboard.server.dao.model.ModelConstants.SCHEDULER_TABLE_NAME;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = SCHEDULER_TABLE_NAME)

public class SchedulerEntity extends AbstractSchedulerEntity<Scheduler>  {
    public SchedulerEntity() {
        super();
    }

    public SchedulerEntity(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public Scheduler toData() {
        return super.toScheduler();
    }

}