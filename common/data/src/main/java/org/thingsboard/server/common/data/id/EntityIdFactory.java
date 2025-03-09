package org.thingsboard.server.common.data.id;

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.edge.EdgeEventType;

import java.util.UUID;

public class EntityIdFactory {

    public static EntityId getByTypeAndUuid(int type, String uuid) {
        return getByTypeAndUuid(EntityType.values()[type], UUID.fromString(uuid));
    }

    public static EntityId getByTypeAndUuid(int type, UUID uuid) {
        return getByTypeAndUuid(EntityType.values()[type], uuid);
    }

    public static EntityId getByTypeAndUuid(String type, String uuid) {
        return getByTypeAndUuid(EntityType.valueOf(type), UUID.fromString(uuid));
    }

    public static EntityId getByTypeAndId(String type, String uuid) {
        return getByTypeAndUuid(EntityType.valueOf(type), UUID.fromString(uuid));
    }

    public static EntityId getByTypeAndUuid(String type, UUID uuid) {
        return getByTypeAndUuid(EntityType.valueOf(type), uuid);
    }

    public static EntityId getByTypeAndUuid(EntityType type, String uuid) {
        return getByTypeAndUuid(type, UUID.fromString(uuid));
    }

    public static EntityId getByTypeAndUuid(EntityType type, UUID uuid) {
        switch (type) {
            case TENANT:
                return TenantId.fromUUID(uuid);
            case CUSTOMER:
                return new CustomerId(uuid);
            case USER:
                return new UserId(uuid);
            case DASHBOARD:
                return new DashboardId(uuid);
            case DEVICE:
                return new DeviceId(uuid);
            case ASSET:
                return new AssetId(uuid);
            case INTEGRATION:
                return new IntegrationId(uuid);
            case DATACONVERTER:
                return new DataConverterId(uuid);
            case ROLES:
                return new RolesId(uuid);
            case VEHICLE:
                return new VehicleId(uuid);
            case SCHEDULER:
                return new SchedulerId(uuid);
            case TRIP:
                return new TripId(uuid);
            case ALARM:
                return new AlarmId(uuid);
            case DRIVER:
                return new DriverId(uuid);
            case REPORT:
                return new ReportId(uuid);
            case RULE_CHAIN:
                return new RuleChainId(uuid);
            case RULE_NODE:
                return new RuleNodeId(uuid);
            case ENTITY_VIEW:
                return new EntityViewId(uuid);
            case WIDGETS_BUNDLE:
                return new WidgetsBundleId(uuid);
            case WIDGET_TYPE:
                return new WidgetTypeId(uuid);
            case DEVICE_PROFILE:
                return new DeviceProfileId(uuid);
            case ASSET_PROFILE:
                return new AssetProfileId(uuid);
            case TENANT_PROFILE:
                return new TenantProfileId(uuid);
            case API_USAGE_STATE:
                return new ApiUsageStateId(uuid);
            case TB_RESOURCE:
                return new TbResourceId(uuid);
            case OTA_PACKAGE:
                return new OtaPackageId(uuid);
            case EDGE:
                return new EdgeId(uuid);
            case RPC:
                return new RpcId(uuid);
            case QUEUE:
                return new QueueId(uuid);
            case NOTIFICATION_TARGET:
                return new NotificationTargetId(uuid);
            case NOTIFICATION_REQUEST:
                return new NotificationRequestId(uuid);
            case NOTIFICATION_RULE:
                return new NotificationRuleId(uuid);
            case NOTIFICATION_TEMPLATE:
                return new NotificationTemplateId(uuid);
            case NOTIFICATION:
                return new NotificationId(uuid);
        }
        throw new IllegalArgumentException("EntityType " + type + " is not supported!");
    }

    public static EntityId getByEdgeEventTypeAndUuid(EdgeEventType edgeEventType, UUID uuid) {
        switch (edgeEventType) {
            case TENANT:
                return new TenantId(uuid);
            case CUSTOMER:
                return new CustomerId(uuid);
            case USER:
                return new UserId(uuid);
            case DASHBOARD:
                return new DashboardId(uuid);
            case DEVICE:
                return new DeviceId(uuid);
            case ASSET:
                return new AssetId(uuid);
            case ROLES:
                return new RolesId(uuid);
            case INTEGRATION:
                return new IntegrationId(uuid);
            case DATACONVERTER:
                return new DataConverterId(uuid);
            case ALARM:
                return new AlarmId(uuid);
            case RULE_CHAIN:
                return new RuleChainId(uuid);
            case ENTITY_VIEW:
                return new EntityViewId(uuid);
            case WIDGETS_BUNDLE:
                return new WidgetsBundleId(uuid);
            case WIDGET_TYPE:
                return new WidgetTypeId(uuid);
            case DEVICE_PROFILE:
                return new DeviceProfileId(uuid);
            case ASSET_PROFILE:
                return new AssetProfileId(uuid);
            case TENANT_PROFILE:
                return new TenantProfileId(uuid);
            case OTA_PACKAGE:
                return new OtaPackageId(uuid);
            case EDGE:
                return new EdgeId(uuid);
            case QUEUE:
                return new QueueId(uuid);
            case TB_RESOURCE:
                return new TbResourceId(uuid);
        }
        throw new IllegalArgumentException("EdgeEventType " + edgeEventType + " is not supported!");
    }

}
