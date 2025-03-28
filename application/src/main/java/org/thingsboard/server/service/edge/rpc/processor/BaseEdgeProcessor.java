
package org.thingsboard.server.service.edge.rpc.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.cluster.TbClusterService;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.edge.EdgeEventType;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageDataIterableByTenantIdEntityId;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainConnectionInfo;
//import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgDataType;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.dao.alarm.AlarmCommentService;
import org.thingsboard.server.dao.alarm.AlarmService;
import org.thingsboard.server.dao.asset.AssetProfileService;
import org.thingsboard.server.dao.asset.AssetService;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.customer.CustomerService;
import org.thingsboard.server.dao.dashboard.DashboardService;
import org.thingsboard.server.dao.device.DeviceCredentialsService;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.edge.EdgeEventService;
import org.thingsboard.server.dao.edge.EdgeService;
import org.thingsboard.server.dao.edge.EdgeSynchronizationManager;
import org.thingsboard.server.dao.entityview.EntityViewService;
import org.thingsboard.server.dao.integration.IntegrationService;
import org.thingsboard.server.dao.ota.OtaPackageService;
import org.thingsboard.server.dao.queue.QueueService;
import org.thingsboard.server.dao.relation.RelationService;
import org.thingsboard.server.dao.resource.ResourceService;
import org.thingsboard.server.dao.roles.RolesService;
import org.thingsboard.server.dao.rule.RuleChainService;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantProfileService;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.dao.user.UserService;
//import org.thingsboard.server.dao.vehicle.VehicleService;
import org.thingsboard.server.dao.widget.WidgetTypeService;
import org.thingsboard.server.dao.widget.WidgetsBundleService;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.TbQueueCallback;
import org.thingsboard.server.queue.TbQueueMsgMetadata;
import org.thingsboard.server.queue.discovery.PartitionService;
import org.thingsboard.server.queue.provider.TbQueueProducerProvider;
import org.thingsboard.server.service.edge.rpc.constructor.alarm.AlarmMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.asset.AssetMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.customer.CustomerMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.dashboard.DashboardMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.device.DeviceMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.edge.EdgeMsgConstructor;
import org.thingsboard.server.service.edge.rpc.constructor.entityview.EntityViewMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.integration.IntegrationMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.ota.OtaPackageMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.queue.QueueMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.relation.RelationMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.resource.ResourceMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.roles.RolesMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.rule.RuleChainMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.settings.AdminSettingsMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.telemetry.EntityDataMsgConstructor;
import org.thingsboard.server.service.edge.rpc.constructor.tenant.TenantMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.user.UserMsgConstructorFactory;
//import org.thingsboard.server.service.edge.rpc.constructor.vehicle.VehicleMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.constructor.widget.WidgetMsgConstructorFactory;
import org.thingsboard.server.service.edge.rpc.processor.alarm.AlarmEdgeProcessorFactory;
import org.thingsboard.server.service.edge.rpc.processor.asset.AssetEdgeProcessorFactory;
import org.thingsboard.server.service.edge.rpc.processor.entityview.EntityViewProcessorFactory;
import org.thingsboard.server.service.edge.rpc.processor.roles.RolesEdgeProcessorFactory;
import org.thingsboard.server.service.entity.TbNotificationEntityService;
import org.thingsboard.server.service.executors.DbCallbackExecutorService;
import org.thingsboard.server.service.profile.TbAssetProfileCache;
import org.thingsboard.server.service.profile.TbDeviceProfileCache;
import org.thingsboard.server.service.state.DefaultDeviceStateService;
import org.thingsboard.server.service.state.DeviceStateService;
import org.thingsboard.server.service.telemetry.TelemetrySubscriptionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public abstract class BaseEdgeProcessor {

    protected static final Lock deviceCreationLock = new ReentrantLock();
    protected static final Lock assetCreationLock = new ReentrantLock();
    protected static final Lock rolesCreationLock = new ReentrantLock();
//    protected static final Lock vehicleCreationLock = new ReentrantLock();
    protected static final Lock integrationCreationLock = new ReentrantLock();

    protected static final int DEFAULT_PAGE_SIZE = 100;

    @Autowired
    protected TelemetrySubscriptionService tsSubService;

    @Autowired
    protected TbNotificationEntityService notificationEntityService;

    @Autowired
    protected RuleChainService ruleChainService;

    @Autowired
    protected AlarmService alarmService;

    @Autowired
    protected AlarmCommentService alarmCommentService;

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected TbDeviceProfileCache deviceProfileCache;

    @Autowired
    protected TbAssetProfileCache assetProfileCache;

    @Autowired
    protected DashboardService dashboardService;

    @Autowired
    protected AssetService assetService;

    @Autowired
    protected IntegrationService integrationService;

    @Autowired
    protected RolesService rolesService;

//    @Autowired
//    protected VehicleService vehicleService;

    @Autowired
    protected EntityViewService entityViewService;

    @Autowired
    protected TenantService tenantService;

    @Autowired
    protected TenantProfileService tenantProfileService;

    @Autowired
    protected EdgeService edgeService;

    @Autowired
    protected CustomerService customerService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected DeviceProfileService deviceProfileService;

    @Autowired
    protected AssetProfileService assetProfileService;

    @Autowired
    protected RelationService relationService;

    @Autowired
    protected DeviceCredentialsService deviceCredentialsService;

    @Autowired
    protected AttributesService attributesService;

    @Autowired
    protected TbClusterService tbClusterService;

    @Autowired
    protected DeviceStateService deviceStateService;

    @Autowired
    protected EdgeEventService edgeEventService;

    @Autowired
    protected WidgetsBundleService widgetsBundleService;

    @Autowired
    protected WidgetTypeService widgetTypeService;

    @Autowired
    protected OtaPackageService otaPackageService;

    @Autowired
    protected QueueService queueService;

    @Autowired
    protected PartitionService partitionService;

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    @Lazy
    protected TbQueueProducerProvider producerProvider;

    @Autowired
    protected DataValidator<Device> deviceValidator;

    @Autowired
    protected DataValidator<DeviceProfile> deviceProfileValidator;

    @Autowired
    protected DataValidator<Asset> assetValidator;

    @Autowired
    protected DataValidator<Integration> integrationValidator;

    @Autowired
    protected DataValidator<Roles> rolesValidator;

//    @Autowired
//    protected DataValidator<Vehicle> vehicleValidator;

    @Autowired
    protected DataValidator<AssetProfile> assetProfileValidator;

    @Autowired
    protected DataValidator<Dashboard> dashboardValidator;

    @Autowired
    protected DataValidator<EntityView> entityViewValidator;

    @Autowired
    protected DataValidator<TbResource> resourceValidator;

    @Autowired
    protected EdgeMsgConstructor edgeMsgConstructor;

    @Autowired
    protected EntityDataMsgConstructor entityDataMsgConstructor;

    @Autowired
    protected RuleChainMsgConstructorFactory ruleChainMsgConstructorFactory;

    @Autowired
    protected AlarmMsgConstructorFactory alarmMsgConstructorFactory;

    @Autowired
    protected DeviceMsgConstructorFactory deviceMsgConstructorFactory;

    @Autowired
    protected AssetMsgConstructorFactory assetMsgConstructorFactory;

    @Autowired
    protected IntegrationMsgConstructorFactory integrationMsgConstructorFactory;

    @Autowired
    protected RolesMsgConstructorFactory rolesMsgConstructorFactory;

//    @Autowired
//    protected VehicleMsgConstructorFactory vehicleMsgConstructorFactory;

    @Autowired
    protected EntityViewMsgConstructorFactory entityViewMsgConstructorFactory;

    @Autowired
    protected DashboardMsgConstructorFactory dashboardMsgConstructorFactory;

    @Autowired
    protected RelationMsgConstructorFactory relationMsgConstructorFactory;

    @Autowired
    protected UserMsgConstructorFactory userMsgConstructorFactory;

    @Autowired
    protected CustomerMsgConstructorFactory customerMsgConstructorFactory;

    @Autowired
    protected TenantMsgConstructorFactory tenantMsgConstructorFactory;

    @Autowired
    protected WidgetMsgConstructorFactory widgetMsgConstructorFactory;

    @Autowired
    protected AdminSettingsMsgConstructorFactory adminSettingsMsgConstructorFactory;

    @Autowired
    protected OtaPackageMsgConstructorFactory otaPackageMsgConstructorFactory;

    @Autowired
    protected QueueMsgConstructorFactory queueMsgConstructorFactory;

    @Autowired
    protected ResourceMsgConstructorFactory resourceMsgConstructorFactory;

    @Autowired
    protected AlarmEdgeProcessorFactory alarmEdgeProcessorFactory;

    @Autowired
    protected AssetEdgeProcessorFactory assetEdgeProcessorFactory;

    @Autowired
    protected RolesEdgeProcessorFactory rolesEdgeProcessorFactory;

//    @Autowired
//    protected VehicleEdgeProcessorFactory vehicleEdgeProcessorFactory;

    @Autowired
    protected EntityViewProcessorFactory entityViewProcessorFactory;

    @Autowired
    protected EdgeSynchronizationManager edgeSynchronizationManager;

    @Autowired
    protected DbCallbackExecutorService dbCallbackExecutorService;

    protected ListenableFuture<Void> saveEdgeEvent(TenantId tenantId,
                                                   EdgeId edgeId,
                                                   EdgeEventType type,
                                                   EdgeEventActionType action,
                                                   EntityId entityId,
                                                   JsonNode body) {
        ListenableFuture<Optional<AttributeKvEntry>> future =
                attributesService.find(tenantId, edgeId, DataConstants.SERVER_SCOPE, DefaultDeviceStateService.ACTIVITY_STATE);
        return Futures.transformAsync(future, activeOpt -> {
            if (activeOpt.isEmpty()) {
                log.trace("Edge is not activated. Skipping event. tenantId [{}], edgeId [{}], type[{}], " +
                                "action [{}], entityId [{}], body [{}]",
                        tenantId, edgeId, type, action, entityId, body);
                return Futures.immediateFuture(null);
            }
            if (activeOpt.get().getBooleanValue().isPresent() && activeOpt.get().getBooleanValue().get()) {
                return doSaveEdgeEvent(tenantId, edgeId, type, action, entityId, body);
            } else {
                if (doSaveIfEdgeIsOffline(type, action)) {
                    return doSaveEdgeEvent(tenantId, edgeId, type, action, entityId, body);
                } else {
                    log.trace("Edge is not active at the moment. Skipping event. tenantId [{}], edgeId [{}], type[{}], " +
                                    "action [{}], entityId [{}], body [{}]",
                            tenantId, edgeId, type, action, entityId, body);
                    return Futures.immediateFuture(null);
                }
            }
        }, dbCallbackExecutorService);
    }

    private boolean doSaveIfEdgeIsOffline(EdgeEventType type,
                                          EdgeEventActionType action) {
        switch (action) {
            case TIMESERIES_UPDATED:
            case ALARM_ACK:
            case ALARM_CLEAR:
            case ALARM_ASSIGNED:
            case ALARM_UNASSIGNED:
            case CREDENTIALS_REQUEST:
            case ADDED_COMMENT:
            case UPDATED_COMMENT:
                return true;
        }
        switch (type) {
            case ALARM:
            case ALARM_COMMENT:
            case RULE_CHAIN:
            case RULE_CHAIN_METADATA:
            case USER:
            case CUSTOMER:
            case TENANT:
            case TENANT_PROFILE:
            case WIDGETS_BUNDLE:
            case WIDGET_TYPE:
            case ADMIN_SETTINGS:
            case OTA_PACKAGE:
            case QUEUE:
            case RELATION:
                return true;
        }
        return false;
    }

    private ListenableFuture<Void> doSaveEdgeEvent(TenantId tenantId, EdgeId edgeId, EdgeEventType type, EdgeEventActionType action, EntityId entityId, JsonNode body) {
        log.debug("Pushing event to edge queue. tenantId [{}], edgeId [{}], type[{}], " +
                        "action [{}], entityId [{}], body [{}]",
                tenantId, edgeId, type, action, entityId, body);

        EdgeEvent edgeEvent = EdgeUtils.constructEdgeEvent(tenantId, edgeId, type, action, entityId, body);

        return Futures.transform(edgeEventService.saveAsync(edgeEvent), unused -> {
            tbClusterService.onEdgeEventUpdate(tenantId, edgeId);
            return null;
        }, dbCallbackExecutorService);
    }

    protected ListenableFuture<Void> processActionForAllEdges(TenantId tenantId, EdgeEventType type,
                                                              EdgeEventActionType actionType, EntityId entityId,
                                                              EdgeId sourceEdgeId) {
        List<ListenableFuture<Void>> futures = new ArrayList<>();
        if (TenantId.SYS_TENANT_ID.equals(tenantId)) {
            PageLink pageLink = new PageLink(DEFAULT_PAGE_SIZE);
            PageData<TenantId> tenantsIds;
            do {
                tenantsIds = tenantService.findTenantsIds(pageLink);
                for (TenantId tenantId1 : tenantsIds.getData()) {
                    futures.addAll(processActionForAllEdgesByTenantId(tenantId1, type, actionType, entityId, null, sourceEdgeId));
                }
                pageLink = pageLink.nextPageLink();
            } while (tenantsIds.hasNext());
        } else {
            futures = processActionForAllEdgesByTenantId(tenantId, type, actionType, entityId, null, sourceEdgeId);
        }
        return Futures.transform(Futures.allAsList(futures), voids -> null, dbCallbackExecutorService);
    }

    private List<ListenableFuture<Void>> processActionForAllEdgesByTenantId(TenantId tenantId,
                                                                            EdgeEventType type,
                                                                            EdgeEventActionType actionType,
                                                                            EntityId entityId,
                                                                            JsonNode body,
                                                                            EdgeId sourceEdgeId) {
        PageLink pageLink = new PageLink(DEFAULT_PAGE_SIZE);
        PageData<Edge> pageData;
        List<ListenableFuture<Void>> futures = new ArrayList<>();
        do {
            pageData = edgeService.findEdgesByTenantId(tenantId, pageLink);
            if (pageData != null && pageData.getData() != null && !pageData.getData().isEmpty()) {
                for (Edge edge : pageData.getData()) {
                    if (!edge.getId().equals(sourceEdgeId)) {
                        futures.add(saveEdgeEvent(tenantId, edge.getId(), type, actionType, entityId, body));
                    }
                }
                if (pageData.hasNext()) {
                    pageLink = pageLink.nextPageLink();
                }
            }
        } while (pageData != null && pageData.hasNext());
        return futures;
    }

    protected ListenableFuture<Void> handleUnsupportedMsgType(UpdateMsgType msgType) {
        String errMsg = String.format("Unsupported msg type %s", msgType);
        log.error(errMsg);
        return Futures.immediateFailedFuture(new RuntimeException(errMsg));
    }

    protected UpdateMsgType getUpdateMsgType(EdgeEventActionType actionType) {
        switch (actionType) {
            case UPDATED:
            case CREDENTIALS_UPDATED:
            case ASSIGNED_TO_CUSTOMER:
            case UNASSIGNED_FROM_CUSTOMER:
            case UPDATED_COMMENT:
                return UpdateMsgType.ENTITY_UPDATED_RPC_MESSAGE;
            case ADDED:
            case ASSIGNED_TO_EDGE:
            case RELATION_ADD_OR_UPDATE:
            case ADDED_COMMENT:
                return UpdateMsgType.ENTITY_CREATED_RPC_MESSAGE;
            case DELETED:
            case UNASSIGNED_FROM_EDGE:
            case RELATION_DELETED:
            case DELETED_COMMENT:
            case ALARM_DELETE:
                return UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE;
            case ALARM_ACK:
                return UpdateMsgType.ALARM_ACK_RPC_MESSAGE;
            case ALARM_CLEAR:
                return UpdateMsgType.ALARM_CLEAR_RPC_MESSAGE;
            default:
                throw new RuntimeException("Unsupported actionType [" + actionType + "]");
        }
    }

    public ListenableFuture<Void> processEntityNotification(TenantId tenantId, TransportProtos.EdgeNotificationMsgProto edgeNotificationMsg) {
        EdgeEventType type = EdgeEventType.valueOf(edgeNotificationMsg.getType());
        EdgeEventActionType actionType = EdgeEventActionType.valueOf(edgeNotificationMsg.getAction());
        EntityId entityId = EntityIdFactory.getByEdgeEventTypeAndUuid(type, new UUID(edgeNotificationMsg.getEntityIdMSB(), edgeNotificationMsg.getEntityIdLSB()));
        EdgeId originatorEdgeId = safeGetEdgeId(edgeNotificationMsg.getOriginatorEdgeIdMSB(), edgeNotificationMsg.getOriginatorEdgeIdLSB());
        if (type.isAllEdgesRelated()) {
            return processEntityNotificationForAllEdges(tenantId, type, actionType, entityId, originatorEdgeId);
        } else {
            JsonNode body = JacksonUtil.toJsonNode(edgeNotificationMsg.getBody());
            EdgeId edgeId = safeGetEdgeId(edgeNotificationMsg.getEdgeIdMSB(), edgeNotificationMsg.getEdgeIdLSB());
            switch (actionType) {
                case UPDATED:
                case CREDENTIALS_UPDATED:
                case ASSIGNED_TO_CUSTOMER:
                case UNASSIGNED_FROM_CUSTOMER:
                    if (edgeId != null) {
                        return saveEdgeEvent(tenantId, edgeId, type, actionType, entityId, body);
                    } else {
                        return processNotificationToRelatedEdges(tenantId, entityId, type, actionType, originatorEdgeId);
                    }
                case DELETED:
                    EdgeEventActionType deleted = EdgeEventActionType.DELETED;
                    if (edgeId != null) {
                        return saveEdgeEvent(tenantId, edgeId, type, deleted, entityId, body);
                    } else {
                        return Futures.transform(Futures.allAsList(processActionForAllEdgesByTenantId(tenantId, type, deleted, entityId, body, originatorEdgeId)),
                                voids -> null, dbCallbackExecutorService);
                    }
                case ASSIGNED_TO_EDGE:
                case UNASSIGNED_FROM_EDGE:
                    if (originatorEdgeId == null) {
                        ListenableFuture<Void> future = saveEdgeEvent(tenantId, edgeId, type, actionType, entityId, body);
                        return Futures.transformAsync(future, unused -> {
                            if (type.equals(EdgeEventType.RULE_CHAIN)) {
                                return updateDependentRuleChains(tenantId, new RuleChainId(entityId.getId()), edgeId);
                            } else {
                                return Futures.immediateFuture(null);
                            }
                        }, dbCallbackExecutorService);
                    } else {
                        return Futures.immediateFuture(null);
                    }
                default:
                    return Futures.immediateFuture(null);
            }
        }
    }

    protected EdgeId safeGetEdgeId(long edgeIdMSB, long edgeIdLSB) {
        if (edgeIdMSB != 0 && edgeIdLSB != 0) {
            return new EdgeId(new UUID(edgeIdMSB, edgeIdLSB));
        } else {
            return null;
        }
    }

    private ListenableFuture<Void> processNotificationToRelatedEdges(TenantId tenantId, EntityId entityId, EdgeEventType type,
                                                                     EdgeEventActionType actionType, EdgeId sourceEdgeId) {
        List<ListenableFuture<Void>> futures = new ArrayList<>();
        PageDataIterableByTenantIdEntityId<EdgeId> edgeIds =
                new PageDataIterableByTenantIdEntityId<>(edgeService::findRelatedEdgeIdsByEntityId, tenantId, entityId, DEFAULT_PAGE_SIZE);
        for (EdgeId relatedEdgeId : edgeIds) {
            if (!relatedEdgeId.equals(sourceEdgeId)) {
                futures.add(saveEdgeEvent(tenantId, relatedEdgeId, type, actionType, entityId, null));
            }
        }
        return Futures.transform(Futures.allAsList(futures), voids -> null, dbCallbackExecutorService);
    }

    private ListenableFuture<Void> updateDependentRuleChains(TenantId tenantId, RuleChainId processingRuleChainId, EdgeId edgeId) {
        PageLink pageLink = new PageLink(DEFAULT_PAGE_SIZE);
        PageData<RuleChain> pageData;
        List<ListenableFuture<Void>> futures = new ArrayList<>();
        do {
            pageData = ruleChainService.findRuleChainsByTenantIdAndEdgeId(tenantId, edgeId, pageLink);
            if (pageData != null && pageData.getData() != null && !pageData.getData().isEmpty()) {
                for (RuleChain ruleChain : pageData.getData()) {
                    if (!ruleChain.getId().equals(processingRuleChainId)) {
                        List<RuleChainConnectionInfo> connectionInfos =
                                ruleChainService.loadRuleChainMetaData(ruleChain.getTenantId(), ruleChain.getId()).getRuleChainConnections();
                        if (connectionInfos != null && !connectionInfos.isEmpty()) {
                            for (RuleChainConnectionInfo connectionInfo : connectionInfos) {
                                if (connectionInfo.getTargetRuleChainId().equals(processingRuleChainId)) {
                                    futures.add(saveEdgeEvent(tenantId,
                                            edgeId,
                                            EdgeEventType.RULE_CHAIN_METADATA,
                                            EdgeEventActionType.UPDATED,
                                            ruleChain.getId(),
                                            null));
                                }
                            }
                        }
                    }
                }
                if (pageData.hasNext()) {
                    pageLink = pageLink.nextPageLink();
                }
            }
        } while (pageData != null && pageData.hasNext());
        return Futures.transform(Futures.allAsList(futures), voids -> null, dbCallbackExecutorService);
    }

    private ListenableFuture<Void> processEntityNotificationForAllEdges(TenantId tenantId, EdgeEventType type, EdgeEventActionType actionType, EntityId entityId, EdgeId sourceEdgeId) {
        switch (actionType) {
            case ADDED:
            case UPDATED:
            case DELETED:
            case CREDENTIALS_UPDATED: // used by USER entity
                return processActionForAllEdges(tenantId, type, actionType, entityId, sourceEdgeId);
            default:
                return Futures.immediateFuture(null);
        }
    }

    protected EntityId constructEntityId(String entityTypeStr, long entityIdMSB, long entityIdLSB) {
        EntityType entityType = EntityType.valueOf(entityTypeStr);
        switch (entityType) {
            case DEVICE:
                return new DeviceId(new UUID(entityIdMSB, entityIdLSB));
            case ASSET:
                return new AssetId(new UUID(entityIdMSB, entityIdLSB));
            case ROLES:
                return new RolesId(new UUID(entityIdMSB, entityIdLSB));
//            case VEHICLE:
//                return new VehicleId(new UUID(entityIdMSB, entityIdLSB));
            case ENTITY_VIEW:
                return new EntityViewId(new UUID(entityIdMSB, entityIdLSB));
            case DASHBOARD:
                return new DashboardId(new UUID(entityIdMSB, entityIdLSB));
            case TENANT:
                return TenantId.fromUUID(new UUID(entityIdMSB, entityIdLSB));
            case CUSTOMER:
                return new CustomerId(new UUID(entityIdMSB, entityIdLSB));
            case USER:
                return new UserId(new UUID(entityIdMSB, entityIdLSB));
            case EDGE:
                return new EdgeId(new UUID(entityIdMSB, entityIdLSB));
            default:
                log.warn("Unsupported entity type [{}] during construct of entity id. entityIdMSB [{}], entityIdLSB [{}]",
                        entityTypeStr, entityIdMSB, entityIdLSB);
                return null;
        }
    }

    protected UUID safeGetUUID(long mSB, long lSB) {
        return mSB != 0 && lSB != 0 ? new UUID(mSB, lSB) : null;
    }

    protected CustomerId safeGetCustomerId(long mSB, long lSB) {
        CustomerId customerId = null;
        UUID customerUUID = safeGetUUID(mSB, lSB);
        if (customerUUID != null) {
            customerId = new CustomerId(customerUUID);
        }
        return customerId;
    }

    protected boolean isEntityExists(TenantId tenantId, EntityId entityId) {
        switch (entityId.getEntityType()) {
            case TENANT:
                return tenantService.findTenantById(tenantId) != null;
            case DEVICE:
                return deviceService.findDeviceById(tenantId, new DeviceId(entityId.getId())) != null;
            case ASSET:
                return assetService.findAssetById(tenantId, new AssetId(entityId.getId())) != null;
            case ROLES:
                return rolesService.findRolesById(tenantId, new RolesId(entityId.getId())) != null;
//            case VEHICLE:
//                return vehicleService.findVehicleById(tenantId, new VehicleId(entityId.getId())) != null;
            case ENTITY_VIEW:
                return entityViewService.findEntityViewById(tenantId, new EntityViewId(entityId.getId())) != null;
            case CUSTOMER:
                return customerService.findCustomerById(tenantId, new CustomerId(entityId.getId())) != null;
            case USER:
                return userService.findUserById(tenantId, new UserId(entityId.getId())) != null;
            case DASHBOARD:
                return dashboardService.findDashboardById(tenantId, new DashboardId(entityId.getId())) != null;
            case EDGE:
                return edgeService.findEdgeById(tenantId, new EdgeId(entityId.getId())) != null;
            default:
                return false;
        }
    }

    protected void createRelationFromEdge(TenantId tenantId, EdgeId edgeId, EntityId entityId) {
        EntityRelation relation = new EntityRelation();
        relation.setFrom(edgeId);
        relation.setTo(entityId);
        relation.setTypeGroup(RelationTypeGroup.COMMON);
        relation.setType(EntityRelation.EDGE_TYPE);
        relationService.saveRelation(tenantId, relation);
    }

    protected TbMsgMetaData getEdgeActionTbMsgMetaData(Edge edge, CustomerId customerId) {
        TbMsgMetaData metaData = new TbMsgMetaData();
        metaData.putValue("edgeId", edge.getId().toString());
        metaData.putValue("edgeName", edge.getName());
        if (customerId != null && !customerId.isNullUid()) {
            metaData.putValue("customerId", customerId.toString());
        }
        return metaData;
    }

    protected void pushEntityEventToRuleEngine(TenantId tenantId, EntityId entityId, CustomerId customerId,
                                               TbMsgType msgType, String msgData, TbMsgMetaData metaData) {
        TbMsg tbMsg = TbMsg.newMsg(msgType, entityId, customerId, metaData, TbMsgDataType.JSON, msgData);
        tbClusterService.pushMsgToRuleEngine(tenantId, entityId, tbMsg, new TbQueueCallback() {
            @Override
            public void onSuccess(TbQueueMsgMetadata metadata) {
                log.debug("[{}] Successfully send ENTITY_CREATED EVENT to rule engine [{}]", tenantId, msgData);
            }

            @Override
            public void onFailure(Throwable t) {
                log.warn("[{}] Failed to send ENTITY_CREATED EVENT to rule engine [{}]", tenantId, msgData, t);
            }
        });
    }

    protected AssetProfile checkIfAssetProfileDefaultFieldsAssignedToEdge(TenantId tenantId, EdgeId edgeId, AssetProfile assetProfile, EdgeVersion edgeVersion) {
        switch (edgeVersion) {
            case V_3_3_3:
            case V_3_3_0:
            case V_3_4_0:
                if (assetProfile.getDefaultDashboardId() != null
                        && isEntityNotAssignedToEdge(tenantId, assetProfile.getDefaultDashboardId(), edgeId)) {
                    assetProfile.setDefaultDashboardId(null);
                }
                if (assetProfile.getDefaultEdgeRuleChainId() != null
                        && isEntityNotAssignedToEdge(tenantId, assetProfile.getDefaultEdgeRuleChainId(), edgeId)) {
                    assetProfile.setDefaultEdgeRuleChainId(null);
                }
                break;
        }
        return assetProfile;
    }

    protected DeviceProfile checkIfDeviceProfileDefaultFieldsAssignedToEdge(TenantId tenantId, EdgeId edgeId, DeviceProfile deviceProfile, EdgeVersion edgeVersion) {
        switch (edgeVersion) {
            case V_3_3_3:
            case V_3_3_0:
            case V_3_4_0:
                if (deviceProfile.getDefaultDashboardId() != null
                        && isEntityNotAssignedToEdge(tenantId, deviceProfile.getDefaultDashboardId(), edgeId)) {
                    deviceProfile.setDefaultDashboardId(null);
                }
                if (deviceProfile.getDefaultEdgeRuleChainId() != null
                        && isEntityNotAssignedToEdge(tenantId, deviceProfile.getDefaultEdgeRuleChainId(), edgeId)) {
                    deviceProfile.setDefaultEdgeRuleChainId(null);
                }
                break;
        }
        return deviceProfile;
    }

    private boolean isEntityNotAssignedToEdge(TenantId tenantId, EntityId entityId, EdgeId edgeId) {
        PageDataIterableByTenantIdEntityId<EdgeId> edgeIds =
                new PageDataIterableByTenantIdEntityId<>(edgeService::findRelatedEdgeIdsByEntityId, tenantId, entityId, DEFAULT_PAGE_SIZE);
        for (EdgeId edgeId1 : edgeIds) {
            if (edgeId1.equals(edgeId)) {
                return false;
            }
        }
        return true;
    }
}
