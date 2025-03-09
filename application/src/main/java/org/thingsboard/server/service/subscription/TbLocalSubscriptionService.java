
package org.thingsboard.server.service.subscription;

import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.msg.queue.TbCallback;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.discovery.event.ClusterTopologyChangeEvent;
import org.thingsboard.server.service.ws.notification.sub.NotificationRequestUpdate;
import org.thingsboard.server.service.ws.notification.sub.NotificationsSubscriptionUpdate;

import java.util.List;

public interface TbLocalSubscriptionService {

    void addSubscription(TbSubscription<?> subscription);

    void onSubEventCallback(TransportProtos.TbEntitySubEventCallbackProto subEventCallback, TbCallback callback);

    void onSubEventCallback(EntityId entityId, int seqNumber, TbEntityUpdatesInfo entityUpdatesInfo, TbCallback empty);

    void cancelSubscription(String sessionId, int subscriptionId);

    void cancelAllSessionSubscriptions(String sessionId);

    void onTimeSeriesUpdate(TransportProtos.TbSubUpdateProto tsUpdate, TbCallback callback);

    void onTimeSeriesUpdate(EntityId entityId, List<TsKvEntry> update, TbCallback callback);

    void onAttributesUpdate(TransportProtos.TbSubUpdateProto attrUpdate, TbCallback callback);

    void onAttributesUpdate(EntityId entityId, String scope, List<TsKvEntry> update, TbCallback callback);

    void onAlarmUpdate(EntityId entityId, AlarmInfo alarm, boolean deleted, TbCallback callback);

    void onAlarmUpdate(TransportProtos.TbAlarmSubUpdateProto update, TbCallback callback);

    void onNotificationUpdate(EntityId entityId, NotificationsSubscriptionUpdate subscriptionUpdate, TbCallback callback);

    void onApplicationEvent(ClusterTopologyChangeEvent event);

    void onCoreStartupMsg(TransportProtos.CoreStartupMsg coreStartupMsg);

    void onNotificationRequestUpdate(TenantId tenantId, NotificationRequestUpdate update, TbCallback callback);

    void onNotificationUpdate(TransportProtos.NotificationsSubUpdateProto notificationsUpdate, TbCallback callback);

}
