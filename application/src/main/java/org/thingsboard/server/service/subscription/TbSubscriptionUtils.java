
package org.thingsboard.server.service.subscription;

import org.apache.commons.lang3.StringUtils;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityIdFactory;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.BasicTsKvEntry;
import org.thingsboard.server.common.data.kv.BooleanDataEntry;
import org.thingsboard.server.common.data.kv.DataType;
import org.thingsboard.server.common.data.kv.DoubleDataEntry;
import org.thingsboard.server.common.data.kv.JsonDataEntry;
import org.thingsboard.server.common.data.kv.KvEntry;
import org.thingsboard.server.common.data.kv.LongDataEntry;
import org.thingsboard.server.common.data.kv.StringDataEntry;
import org.thingsboard.server.common.data.kv.TsKvEntry;
import org.thingsboard.server.common.data.plugin.ComponentLifecycleEvent;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.gen.transport.TransportProtos.KeyValueProto;
import org.thingsboard.server.gen.transport.TransportProtos.KeyValueType;
import org.thingsboard.server.gen.transport.TransportProtos.SubscriptionMgrMsgProto;
import org.thingsboard.server.gen.transport.TransportProtos.TbAlarmDeleteProto;
import org.thingsboard.server.gen.transport.TransportProtos.TbAlarmUpdateProto;
import org.thingsboard.server.gen.transport.TransportProtos.TbAttributeDeleteProto;
import org.thingsboard.server.gen.transport.TransportProtos.TbAttributeUpdateProto;
import org.thingsboard.server.gen.transport.TransportProtos.TbEntitySubEventProto;
import org.thingsboard.server.gen.transport.TransportProtos.TbTimeSeriesDeleteProto;
import org.thingsboard.server.gen.transport.TransportProtos.TbTimeSeriesUpdateProto;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.TsKvProto;
import org.thingsboard.server.service.ws.notification.sub.NotificationRequestUpdate;
import org.thingsboard.server.service.ws.notification.sub.NotificationUpdate;
import org.thingsboard.server.service.ws.notification.sub.NotificationsSubscriptionUpdate;
import org.thingsboard.server.service.ws.telemetry.sub.AlarmSubscriptionUpdate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class TbSubscriptionUtils {

    public static ToCoreMsg toSubEventProto(String serviceId, TbEntitySubEvent event) {
        SubscriptionMgrMsgProto.Builder msgBuilder = SubscriptionMgrMsgProto.newBuilder();
        var builder = TbEntitySubEventProto.newBuilder()
                .setServiceId(serviceId)
                .setSeqNumber(event.getSeqNumber())
                .setTenantIdMSB(event.getTenantId().getId().getMostSignificantBits())
                .setTenantIdLSB(event.getTenantId().getId().getLeastSignificantBits())
                .setEntityType(event.getEntityId().getEntityType().name())
                .setEntityIdMSB(event.getEntityId().getId().getMostSignificantBits())
                .setEntityIdLSB(event.getEntityId().getId().getLeastSignificantBits())
                .setType(event.getType().name());
        TbSubscriptionsInfo info = event.getInfo();
        if (info != null) {
            builder.setNotifications(info.notifications)
                    .setAlarms(info.alarms)
                    .setTsAllKeys(info.tsAllKeys)
                    .setAttrAllKeys(info.attrAllKeys);
            if (info.tsKeys != null) {
                builder.addAllTsKeys(info.tsKeys);
            }
            if (info.attrKeys != null) {
                builder.addAllAttrKeys(info.attrKeys);
            }
        }
        msgBuilder.setSubEvent(builder);
        return ToCoreMsg.newBuilder().setToSubscriptionMgrMsg(msgBuilder).build();
    }

    public static ToCoreNotificationMsg toProto(UUID id, int seqNumber, TbEntityUpdatesInfo update) {
        TransportProtos.TbEntitySubEventCallbackProto.Builder updateProto = TransportProtos.TbEntitySubEventCallbackProto.newBuilder()
                .setEntityIdMSB(id.getMostSignificantBits())
                .setEntityIdLSB(id.getLeastSignificantBits())
                .setSeqNumber(seqNumber)
                .setAttributesUpdateTs(update.attributesUpdateTs)
                .setTimeSeriesUpdateTs(update.timeSeriesUpdateTs);
        return ToCoreNotificationMsg.newBuilder()
                .setToLocalSubscriptionServiceMsg(
                        TransportProtos.LocalSubscriptionServiceMsgProto.newBuilder()
                                .setSubEventCallback(updateProto)
                                .build())
                .build();
    }


    public static TbEntitySubEvent fromProto(TbEntitySubEventProto proto) {
        ComponentLifecycleEvent event = ComponentLifecycleEvent.valueOf(proto.getType());
        var builder = TbEntitySubEvent.builder()
                .tenantId(TenantId.fromUUID(new UUID(proto.getTenantIdMSB(), proto.getTenantIdLSB())))
                .seqNumber(proto.getSeqNumber())
                .entityId(EntityIdFactory.getByTypeAndUuid(proto.getEntityType(), new UUID(proto.getEntityIdMSB(), proto.getEntityIdLSB())))
                .type(event);
        if (!ComponentLifecycleEvent.DELETED.equals(event)) {
            builder.info(new TbSubscriptionsInfo(proto.getNotifications(), proto.getAlarms(),
                    proto.getTsAllKeys(), proto.getTsKeysCount() > 0 ? new HashSet<>(proto.getTsKeysList()) : null,
                    proto.getAttrAllKeys(), proto.getAttrKeysCount() > 0 ? new HashSet<>(proto.getAttrKeysList()) : null,
                    proto.getSeqNumber()));
        }
        return builder.build();
    }

    public static AlarmSubscriptionUpdate fromProto(TransportProtos.TbAlarmSubUpdateProto proto) {
        if (proto.getErrorCode() > 0) {
            return new AlarmSubscriptionUpdate(SubscriptionErrorCode.forCode(proto.getErrorCode()), proto.getErrorMsg());
        } else {
            AlarmInfo alarm = JacksonUtil.fromString(proto.getAlarm(), AlarmInfo.class);
            return new AlarmSubscriptionUpdate(alarm, proto.getDeleted());
        }
    }

    public static NotificationsSubscriptionUpdate fromProto(TransportProtos.NotificationsSubUpdateProto proto) {
        NotificationsSubscriptionUpdate update;
        if (StringUtils.isNotEmpty(proto.getNotificationUpdate())) {
            NotificationUpdate notificationUpdate = JacksonUtil.fromString(proto.getNotificationUpdate(), NotificationUpdate.class);
            update = new NotificationsSubscriptionUpdate(notificationUpdate);
        } else {
            NotificationRequestUpdate notificationRequestUpdate = JacksonUtil.fromString(proto.getNotificationRequestUpdate(), NotificationRequestUpdate.class);
            update = new NotificationsSubscriptionUpdate(notificationRequestUpdate);
        }
        return update;
    }

    public static ToCoreNotificationMsg toAlarmSubUpdateToProto(EntityId entityId, AlarmInfo alarmInfo, boolean deleted) {
        TransportProtos.TbAlarmSubUpdateProto.Builder updateProto = TransportProtos.TbAlarmSubUpdateProto.newBuilder()
                .setEntityIdMSB(entityId.getId().getMostSignificantBits())
                .setEntityIdLSB(entityId.getId().getLeastSignificantBits())
                .setAlarm(JacksonUtil.toString(alarmInfo))
                .setDeleted(deleted);
        return ToCoreNotificationMsg.newBuilder()
                .setToLocalSubscriptionServiceMsg(
                        TransportProtos.LocalSubscriptionServiceMsgProto.newBuilder()
                                .setAlarmUpdate(updateProto)
                                .build())
                .build();
    }

    public static ToCoreNotificationMsg notificationsSubUpdateToProto(EntityId entityId, NotificationsSubscriptionUpdate update) {
        TransportProtos.NotificationsSubUpdateProto.Builder updateProto = TransportProtos.NotificationsSubUpdateProto.newBuilder()
                .setEntityIdMSB(entityId.getId().getMostSignificantBits())
                .setEntityIdLSB(entityId.getId().getLeastSignificantBits());
        if (update.getNotificationUpdate() != null) {
            updateProto.setNotificationUpdate(JacksonUtil.toString(update.getNotificationUpdate()));
        }
        if (update.getNotificationRequestUpdate() != null) {
            updateProto.setNotificationRequestUpdate(JacksonUtil.toString(update.getNotificationRequestUpdate()));
        }
        return ToCoreNotificationMsg.newBuilder()
                .setToLocalSubscriptionServiceMsg(TransportProtos.LocalSubscriptionServiceMsgProto.newBuilder()
                        .setNotificationsUpdate(updateProto)
                        .build())
                .build();
    }

    public static ToCoreMsg toTimeseriesUpdateProto(TenantId tenantId, EntityId entityId, List<TsKvEntry> ts) {
        TbTimeSeriesUpdateProto.Builder builder = TbTimeSeriesUpdateProto.newBuilder();
        builder.setEntityType(entityId.getEntityType().name());
        builder.setEntityIdMSB(entityId.getId().getMostSignificantBits());
        builder.setEntityIdLSB(entityId.getId().getLeastSignificantBits());
        builder.setTenantIdMSB(tenantId.getId().getMostSignificantBits());
        builder.setTenantIdLSB(tenantId.getId().getLeastSignificantBits());
        ts.forEach(v -> builder.addData(toKeyValueProto(v.getTs(), v).build()));
        SubscriptionMgrMsgProto.Builder msgBuilder = SubscriptionMgrMsgProto.newBuilder();
        msgBuilder.setTsUpdate(builder);
        return ToCoreMsg.newBuilder().setToSubscriptionMgrMsg(msgBuilder.build()).build();
    }

    public static ToCoreMsg toTimeseriesDeleteProto(TenantId tenantId, EntityId entityId, List<String> keys) {
        TbTimeSeriesDeleteProto.Builder builder = TbTimeSeriesDeleteProto.newBuilder();
        builder.setEntityType(entityId.getEntityType().name());
        builder.setEntityIdMSB(entityId.getId().getMostSignificantBits());
        builder.setEntityIdLSB(entityId.getId().getLeastSignificantBits());
        builder.setTenantIdMSB(tenantId.getId().getMostSignificantBits());
        builder.setTenantIdLSB(tenantId.getId().getLeastSignificantBits());
        builder.addAllKeys(keys);
        SubscriptionMgrMsgProto.Builder msgBuilder = SubscriptionMgrMsgProto.newBuilder();
        msgBuilder.setTsDelete(builder);
        return ToCoreMsg.newBuilder().setToSubscriptionMgrMsg(msgBuilder.build()).build();
    }

    public static ToCoreMsg toAttributesUpdateProto(TenantId tenantId, EntityId entityId, String scope, List<AttributeKvEntry> attributes) {
        TbAttributeUpdateProto.Builder builder = TbAttributeUpdateProto.newBuilder();
        builder.setEntityType(entityId.getEntityType().name());
        builder.setEntityIdMSB(entityId.getId().getMostSignificantBits());
        builder.setEntityIdLSB(entityId.getId().getLeastSignificantBits());
        builder.setTenantIdMSB(tenantId.getId().getMostSignificantBits());
        builder.setTenantIdLSB(tenantId.getId().getLeastSignificantBits());
        builder.setScope(scope);
        attributes.forEach(v -> builder.addData(toKeyValueProto(v.getLastUpdateTs(), v).build()));

        SubscriptionMgrMsgProto.Builder msgBuilder = SubscriptionMgrMsgProto.newBuilder();
        msgBuilder.setAttrUpdate(builder);
        return ToCoreMsg.newBuilder().setToSubscriptionMgrMsg(msgBuilder.build()).build();
    }

    public static ToCoreMsg toAttributesDeleteProto(TenantId tenantId, EntityId entityId, String scope, List<String> keys, boolean notifyDevice) {
        TbAttributeDeleteProto.Builder builder = TbAttributeDeleteProto.newBuilder();
        builder.setEntityType(entityId.getEntityType().name());
        builder.setEntityIdMSB(entityId.getId().getMostSignificantBits());
        builder.setEntityIdLSB(entityId.getId().getLeastSignificantBits());
        builder.setTenantIdMSB(tenantId.getId().getMostSignificantBits());
        builder.setTenantIdLSB(tenantId.getId().getLeastSignificantBits());
        builder.setScope(scope);
        builder.addAllKeys(keys);
        builder.setNotifyDevice(notifyDevice);

        SubscriptionMgrMsgProto.Builder msgBuilder = SubscriptionMgrMsgProto.newBuilder();
        msgBuilder.setAttrDelete(builder);
        return ToCoreMsg.newBuilder().setToSubscriptionMgrMsg(msgBuilder.build()).build();
    }


    private static TsKvProto.Builder toKeyValueProto(long ts, KvEntry attr) {
        KeyValueProto.Builder dataBuilder = KeyValueProto.newBuilder();
        dataBuilder.setKey(attr.getKey());
        dataBuilder.setType(KeyValueType.forNumber(attr.getDataType().ordinal()));
        switch (attr.getDataType()) {
            case BOOLEAN:
                attr.getBooleanValue().ifPresent(dataBuilder::setBoolV);
                break;
            case LONG:
                attr.getLongValue().ifPresent(dataBuilder::setLongV);
                break;
            case DOUBLE:
                attr.getDoubleValue().ifPresent(dataBuilder::setDoubleV);
                break;
            case JSON:
                attr.getJsonValue().ifPresent(dataBuilder::setJsonV);
                break;
            case STRING:
                attr.getStrValue().ifPresent(dataBuilder::setStringV);
                break;
        }
        return TsKvProto.newBuilder().setTs(ts).setKv(dataBuilder);
    }

    private static TransportProtos.TsValueProto toTsValueProto(long ts, KvEntry attr) {
        TransportProtos.TsValueProto.Builder dataBuilder = TransportProtos.TsValueProto.newBuilder();
        dataBuilder.setTs(ts);
        dataBuilder.setType(KeyValueType.forNumber(attr.getDataType().ordinal()));
        switch (attr.getDataType()) {
            case BOOLEAN:
                attr.getBooleanValue().ifPresent(dataBuilder::setBoolV);
                break;
            case LONG:
                attr.getLongValue().ifPresent(dataBuilder::setLongV);
                break;
            case DOUBLE:
                attr.getDoubleValue().ifPresent(dataBuilder::setDoubleV);
                break;
            case JSON:
                attr.getJsonValue().ifPresent(dataBuilder::setJsonV);
                break;
            case STRING:
                attr.getStrValue().ifPresent(dataBuilder::setStringV);
                break;
        }
        return dataBuilder.build();
    }


    public static EntityId toEntityId(String entityType, long entityIdMSB, long entityIdLSB) {
        return EntityIdFactory.getByTypeAndUuid(entityType, new UUID(entityIdMSB, entityIdLSB));
    }

    public static List<TsKvEntry> toTsKvEntityList(List<TsKvProto> dataList) {
        List<TsKvEntry> result = new ArrayList<>(dataList.size());
        dataList.forEach(proto -> result.add(new BasicTsKvEntry(proto.getTs(), getKvEntry(proto.getKv()))));
        return result;
    }

    public static List<AttributeKvEntry> toAttributeKvList(List<TsKvProto> dataList) {
        List<AttributeKvEntry> result = new ArrayList<>(dataList.size());
        dataList.forEach(proto -> result.add(new BaseAttributeKvEntry(getKvEntry(proto.getKv()), proto.getTs())));
        return result;
    }

    private static KvEntry getKvEntry(KeyValueProto proto) {
        KvEntry entry = null;
        DataType type = DataType.values()[proto.getType().getNumber()];
        switch (type) {
            case BOOLEAN:
                entry = new BooleanDataEntry(proto.getKey(), proto.getBoolV());
                break;
            case LONG:
                entry = new LongDataEntry(proto.getKey(), proto.getLongV());
                break;
            case DOUBLE:
                entry = new DoubleDataEntry(proto.getKey(), proto.getDoubleV());
                break;
            case STRING:
                entry = new StringDataEntry(proto.getKey(), proto.getStringV());
                break;
            case JSON:
                entry = new JsonDataEntry(proto.getKey(), proto.getJsonV());
                break;
        }
        return entry;
    }

    public static List<TsKvEntry> toTsKvEntityList(String key, List<TransportProtos.TsValueProto> dataList) {
        List<TsKvEntry> result = new ArrayList<>(dataList.size());
        dataList.forEach(proto -> result.add(new BasicTsKvEntry(proto.getTs(), getKvEntry(key, proto))));
        return result;
    }

    private static KvEntry getKvEntry(String key, TransportProtos.TsValueProto proto) {
        KvEntry entry = null;
        DataType type = DataType.values()[proto.getType().getNumber()];
        switch (type) {
            case BOOLEAN:
                entry = new BooleanDataEntry(key, proto.getBoolV());
                break;
            case LONG:
                entry = new LongDataEntry(key, proto.getLongV());
                break;
            case DOUBLE:
                entry = new DoubleDataEntry(key, proto.getDoubleV());
                break;
            case STRING:
                entry = new StringDataEntry(key, proto.getStringV());
                break;
            case JSON:
                entry = new JsonDataEntry(key, proto.getJsonV());
                break;
        }
        return entry;
    }

    public static ToCoreMsg toAlarmUpdateProto(TenantId tenantId, EntityId entityId, AlarmInfo alarm) {
        TbAlarmUpdateProto.Builder builder = TbAlarmUpdateProto.newBuilder();
        builder.setEntityType(entityId.getEntityType().name());
        builder.setEntityIdMSB(entityId.getId().getMostSignificantBits());
        builder.setEntityIdLSB(entityId.getId().getLeastSignificantBits());
        builder.setTenantIdMSB(tenantId.getId().getMostSignificantBits());
        builder.setTenantIdLSB(tenantId.getId().getLeastSignificantBits());
        builder.setAlarm(JacksonUtil.toString(alarm));
        SubscriptionMgrMsgProto.Builder msgBuilder = SubscriptionMgrMsgProto.newBuilder();
        msgBuilder.setAlarmUpdate(builder);
        return ToCoreMsg.newBuilder().setToSubscriptionMgrMsg(msgBuilder.build()).build();
    }

    public static ToCoreMsg toAlarmDeletedProto(TenantId tenantId, EntityId entityId, AlarmInfo alarm) {
        TbAlarmDeleteProto.Builder builder = TbAlarmDeleteProto.newBuilder();
        builder.setEntityType(entityId.getEntityType().name());
        builder.setEntityIdMSB(entityId.getId().getMostSignificantBits());
        builder.setEntityIdLSB(entityId.getId().getLeastSignificantBits());
        builder.setTenantIdMSB(tenantId.getId().getMostSignificantBits());
        builder.setTenantIdLSB(tenantId.getId().getLeastSignificantBits());
        builder.setAlarm(JacksonUtil.toString(alarm));
        SubscriptionMgrMsgProto.Builder msgBuilder = SubscriptionMgrMsgProto.newBuilder();
        msgBuilder.setAlarmDelete(builder);
        return ToCoreMsg.newBuilder().setToSubscriptionMgrMsg(msgBuilder.build()).build();
    }

    public static ToCoreMsg notificationUpdateToProto(TenantId tenantId, UserId recipientId, NotificationUpdate notificationUpdate) {
        TransportProtos.NotificationUpdateProto updateProto = TransportProtos.NotificationUpdateProto.newBuilder()
                .setTenantIdMSB(tenantId.getId().getMostSignificantBits())
                .setTenantIdLSB(tenantId.getId().getLeastSignificantBits())
                .setRecipientIdMSB(recipientId.getId().getMostSignificantBits())
                .setRecipientIdLSB(recipientId.getId().getLeastSignificantBits())
                .setUpdate(JacksonUtil.toString(notificationUpdate))
                .build();
        return ToCoreMsg.newBuilder()
                .setToSubscriptionMgrMsg(SubscriptionMgrMsgProto.newBuilder()
                        .setNotificationUpdate(updateProto)
                        .build())
                .build();
    }

    public static ToCoreNotificationMsg notificationRequestUpdateToProto(TenantId tenantId, NotificationRequestUpdate notificationRequestUpdate) {
        TransportProtos.NotificationRequestUpdateProto updateProto = TransportProtos.NotificationRequestUpdateProto.newBuilder()
                .setTenantIdMSB(tenantId.getId().getMostSignificantBits())
                .setTenantIdLSB(tenantId.getId().getLeastSignificantBits())
                .setUpdate(JacksonUtil.toString(notificationRequestUpdate))
                .build();
        return ToCoreNotificationMsg.newBuilder()
                .setToSubscriptionMgrMsg(SubscriptionMgrMsgProto.newBuilder()
                        .setNotificationRequestUpdate(updateProto)
                        .build())
                .build();
    }

    public static List<TsKvEntry> fromProto(TransportProtos.TbSubUpdateProto proto) {
        List<TsKvEntry> result = new ArrayList<>();
        for (var p : proto.getDataList()) {
            result.addAll(toTsKvEntityList(p.getKey(), p.getTsValueList()));
        }
        return result;
    }

    static ToCoreNotificationMsg toProto(EntityId entityId, List<TsKvEntry> updates) {
        return toProto(true, null, entityId, updates);
    }

    static ToCoreNotificationMsg toProto(String scope, EntityId entityId, List<TsKvEntry> updates) {
        return toProto(false, scope, entityId, updates);
    }

    static ToCoreNotificationMsg toProto(boolean timeSeries, String scope, EntityId entityId, List<TsKvEntry> updates) {
        TransportProtos.TbSubUpdateProto.Builder builder = TransportProtos.TbSubUpdateProto.newBuilder();

        builder.setEntityIdMSB(entityId.getId().getMostSignificantBits());
        builder.setEntityIdLSB(entityId.getId().getLeastSignificantBits());

        Map<String, List<TransportProtos.TsValueProto>> data = new TreeMap<>();

        for (TsKvEntry tsEntry : updates) {
            data.computeIfAbsent(tsEntry.getKey(), k -> new ArrayList<>()).add(toTsValueProto(tsEntry.getTs(), tsEntry));
        }

        data.forEach((key, value) -> {
            TransportProtos.TsValueListProto.Builder dataBuilder = TransportProtos.TsValueListProto.newBuilder();
            dataBuilder.setKey(key);
            dataBuilder.addAllTsValue(value);
            builder.addData(dataBuilder.build());
        });

        var result = TransportProtos.LocalSubscriptionServiceMsgProto.newBuilder();
        if (timeSeries) {
            result.setTsUpdate(builder);
        } else {
            builder.setScope(scope);
            result.setAttrUpdate(builder);
        }
        return ToCoreNotificationMsg.newBuilder().setToLocalSubscriptionServiceMsg(result).build();
    }

}
