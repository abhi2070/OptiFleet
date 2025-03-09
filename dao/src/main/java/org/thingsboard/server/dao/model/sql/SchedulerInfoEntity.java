package org.thingsboard.server.dao.model.sql;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.asset.AssetInfo;
import org.thingsboard.server.common.data.scheduler.SchedulerInfo;

import java.util.HashMap;
import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = true)
public class SchedulerInfoEntity extends AbstractSchedulerEntity<SchedulerInfo>{

    public static final Map<String,String> schedulerInfoColumnMap = new HashMap<>();
    static {
        schedulerInfoColumnMap.put("customerTitle", "c.title");
    }

    public SchedulerInfoEntity() {
        super();
    }

    public SchedulerInfoEntity(SchedulerEntity schedulerEntity) {
        super(schedulerEntity);
    }

    @Override
    public SchedulerInfo toData() {
        return new SchedulerInfo(super.toScheduler());
    }
}
