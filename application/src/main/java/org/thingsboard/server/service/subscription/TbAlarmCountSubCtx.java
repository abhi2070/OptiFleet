
package org.thingsboard.server.service.subscription;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.query.AlarmCountQuery;
import org.thingsboard.server.dao.alarm.AlarmService;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.entity.EntityService;
import org.thingsboard.server.service.ws.WebSocketService;
import org.thingsboard.server.service.ws.WebSocketSessionRef;
import org.thingsboard.server.service.ws.telemetry.cmd.v2.AlarmCountUpdate;

@Slf4j
@ToString(callSuper = true)
public class TbAlarmCountSubCtx extends TbAbstractSubCtx<AlarmCountQuery> {

    private final AlarmService alarmService;

    @Getter
    @Setter
    private volatile int result;

    public TbAlarmCountSubCtx(String serviceId, WebSocketService wsService,
                              EntityService entityService, TbLocalSubscriptionService localSubscriptionService,
                              AttributesService attributesService, SubscriptionServiceStatistics stats, AlarmService alarmService,
                              WebSocketSessionRef sessionRef, int cmdId) {
        super(serviceId, wsService, entityService, localSubscriptionService, attributesService, stats, sessionRef, cmdId);
        this.alarmService = alarmService;
    }

    @Override
    public void fetchData() {
        result = (int) alarmService.countAlarmsByQuery(getTenantId(), getCustomerId(), query);
        sendWsMsg(new AlarmCountUpdate(cmdId, result));
    }

    @Override
    protected void update() {
        int newCount = (int) alarmService.countAlarmsByQuery(getTenantId(), getCustomerId(), query);
        if (newCount != result) {
            result = newCount;
            sendWsMsg(new AlarmCountUpdate(cmdId, result));
        }
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
