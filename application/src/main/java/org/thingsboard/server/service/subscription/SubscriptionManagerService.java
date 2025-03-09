
package org.thingsboard.server.service.subscription;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.msg.queue.TbCallback;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.discovery.event.OtherServiceShutdownEvent;
import org.thingsboard.server.queue.discovery.event.PartitionChangeEvent;
import org.thingsboard.server.service.ws.notification.sub.NotificationRequestUpdate;
import org.thingsboard.server.service.ws.notification.sub.NotificationUpdate;

import java.util.List;

public interface SubscriptionManagerService extends ApplicationListener<PartitionChangeEvent> {

    void onSubEvent(String serviceId, TbEntitySubEvent event, TbCallback empty);

    void onApplicationEvent(OtherServiceShutdownEvent event);

    void onTimeSeriesUpdate(TenantId tenantId, EntityId entityId, List<TsKvEntry> ts, TbCallback callback);

    void onAttributesUpdate(TenantId tenantId, EntityId entityId, String scope, List<AttributeKvEntry> attributes, TbCallback callback);

    void onAttributesUpdate(TenantId tenantId, EntityId entityId, String scope, List<AttributeKvEntry> attributes, boolean notifyDevice, TbCallback callback);

    void onAttributesDelete(TenantId tenantId, EntityId entityId, String scope, List<String> keys, boolean notifyDevice, TbCallback empty);

    void onTimeSeriesDelete(TenantId tenantId, EntityId entityId, List<String> keys, TbCallback callback);

    void onAlarmUpdate(TenantId tenantId, EntityId entityId, AlarmInfo alarm, TbCallback callback);

    void onAlarmDeleted(TenantId tenantId, EntityId entityId, AlarmInfo alarm, TbCallback callback);

    void onNotificationUpdate(TenantId tenantId, UserId recipientId, NotificationUpdate notificationUpdate, TbCallback callback);

}
