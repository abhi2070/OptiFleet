package org.thingsboard.server.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.common.util.DonAsynchron;
import org.thingsboard.server.cluster.TbClusterService;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmComment;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.asset.AssetInfo;
import org.thingsboard.server.common.data.asset.AssetProfile;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.data_converter.DataConverter;
import org.thingsboard.server.common.data.data_converter.DataConverterInfo;
import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeInfo;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.integration.IntegrationInfo;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.page.SortOrder;
import org.thingsboard.server.common.data.page.TimePageLink;
import org.thingsboard.server.common.data.plugin.ComponentDescriptor;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.query.EntityDataSortOrder;
import org.thingsboard.server.common.data.query.EntityKey;
import org.thingsboard.server.common.data.queue.Queue;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.roles.RolesInfo;
import org.thingsboard.server.common.data.rpc.Rpc;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.common.data.rule.RuleNode;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.common.data.scheduler.SchedulerInfo;
import org.thingsboard.server.common.data.util.ThrowingBiFunction;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.dao.alarm.AlarmCommentService;
import org.thingsboard.server.dao.asset.AssetProfileService;
import org.thingsboard.server.dao.asset.AssetService;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.audit.AuditLogService;
import org.thingsboard.server.dao.customer.CustomerService;
import org.thingsboard.server.dao.dashboard.DashboardService;
import org.thingsboard.server.dao.data_converter.DataConverterService;
import org.thingsboard.server.dao.device.ClaimDevicesService;
import org.thingsboard.server.dao.device.DeviceCredentialsService;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.driver.DriverService;
import org.thingsboard.server.dao.edge.EdgeService;
import org.thingsboard.server.dao.entityview.EntityViewService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.integration.IntegrationService;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.oauth2.OAuth2ConfigTemplateService;
import org.thingsboard.server.dao.oauth2.OAuth2Service;
import org.thingsboard.server.dao.ota.OtaPackageService;
import org.thingsboard.server.dao.queue.QueueService;
import org.thingsboard.server.dao.relation.RelationService;
import org.thingsboard.server.dao.report.ReportService;
import org.thingsboard.server.dao.resource.ResourceService;
import org.thingsboard.server.dao.roles.RolesService;
import org.thingsboard.server.dao.rpc.RpcService;
import org.thingsboard.server.dao.rule.RuleChainService;
import org.thingsboard.server.dao.scheduler.SchedulerService;
import org.thingsboard.server.dao.service.ConstraintValidator;
import org.thingsboard.server.dao.service.Validator;
import org.thingsboard.server.dao.tenant.TbTenantProfileCache;
import org.thingsboard.server.dao.tenant.TenantProfileService;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.dao.trip.TripService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.vehicle.VehicleService;
import org.thingsboard.server.dao.widget.WidgetTypeService;
import org.thingsboard.server.dao.widget.WidgetsBundleService;
import org.thingsboard.server.exception.ThingsboardErrorResponseHandler;
import org.thingsboard.server.queue.discovery.PartitionService;
import org.thingsboard.server.queue.provider.TbQueueProducerProvider;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.action.EntityActionService;
import org.thingsboard.server.service.component.ComponentDiscoveryService;
import org.thingsboard.server.service.entity.TbNotificationEntityService;
import org.thingsboard.server.service.entity.user.TbUserSettingsService;
import org.thingsboard.server.service.ota.OtaPackageStateService;
import org.thingsboard.server.service.profile.TbAssetProfileCache;
import org.thingsboard.server.service.profile.TbDeviceProfileCache;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.AccessControlService;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.security.permission.Resource;
import org.thingsboard.server.service.state.DeviceStateService;
import org.thingsboard.server.service.sync.ie.exporting.ExportableEntitiesService;
import org.thingsboard.server.service.sync.vc.EntitiesVersionControlService;
import org.thingsboard.server.service.telemetry.AlarmSubscriptionService;
import org.thingsboard.server.service.telemetry.TelemetrySubscriptionService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.thingsboard.server.common.data.StringUtils.isNotEmpty;
import static org.thingsboard.server.common.data.query.EntityKeyType.ENTITY_FIELD;
import static org.thingsboard.server.controller.UserController.YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION;
import static org.thingsboard.server.dao.service.Validator.validateId;

@TbCoreComponent
public abstract class BaseController {

    private final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    /*Swagger UI description*/

    @Autowired
    private ThingsboardErrorResponseHandler errorResponseHandler;

    @Autowired
    protected AccessControlService accessControlService;

    @Autowired
    protected TenantService tenantService;

    @Autowired
    protected TenantProfileService tenantProfileService;

    @Autowired
    protected DriverService driverService;

    @Autowired
    protected CustomerService customerService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected TbUserSettingsService userSettingsService;

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected DeviceProfileService deviceProfileService;

    @Autowired
    protected AssetService assetService;

    @Autowired
    protected IntegrationService integrationService;

    @Autowired
    protected DataConverterService dataConverterService;

    @Autowired
    protected SchedulerService schedulerService;

    @Autowired
    protected AssetProfileService assetProfileService;

    @Autowired
    protected AlarmSubscriptionService alarmService;

    @Autowired
    protected AlarmCommentService alarmCommentService;

    @Autowired
    protected DeviceCredentialsService deviceCredentialsService;

    @Autowired
    protected WidgetsBundleService widgetsBundleService;

    @Autowired
    protected WidgetTypeService widgetTypeService;

    @Autowired
    protected DashboardService dashboardService;

    @Autowired
    protected ReportService reportService;

    @Autowired
    protected OAuth2Service oAuth2Service;

    @Autowired
    protected OAuth2ConfigTemplateService oAuth2ConfigTemplateService;

    @Autowired
    protected ComponentDiscoveryService componentDescriptorService;

    @Autowired
    protected RuleChainService ruleChainService;

    @Autowired
    protected TbClusterService tbClusterService;

    @Autowired
    protected RelationService relationService;

    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    protected DeviceStateService deviceStateService;

    @Autowired
    protected EntityViewService entityViewService;

    @Autowired
    protected TelemetrySubscriptionService tsSubService;

    @Autowired
    protected AttributesService attributesService;

    @Autowired
    protected ClaimDevicesService claimDevicesService;

    @Autowired
    protected PartitionService partitionService;

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    protected OtaPackageService otaPackageService;

    @Autowired
    protected OtaPackageStateService otaPackageStateService;

    @Autowired
    protected RpcService rpcService;

    @Autowired
    protected TripService tripService;

    @Autowired
    protected RolesService rolesService;

    @Autowired
    protected VehicleService vehicleService;

    @Autowired
    protected TbQueueProducerProvider producerProvider;

    @Autowired
    protected TbTenantProfileCache tenantProfileCache;

    @Autowired
    protected TbDeviceProfileCache deviceProfileCache;

    @Autowired
    protected TbAssetProfileCache assetProfileCache;

    @Autowired(required = false)
    protected EdgeService edgeService;

    @Autowired
    protected TbNotificationEntityService notificationEntityService;

    @Autowired
    protected EntityActionService entityActionService;

    @Autowired
    protected QueueService queueService;

    @Autowired
    protected EntitiesVersionControlService vcService;

    @Autowired
    protected ExportableEntitiesService entitiesService;

    @Value("${server.log_controller_error_stack_trace}")
    @Getter
    private boolean logControllerErrorStackTrace;

    @Value("${edges.enabled}")
    @Getter
    protected boolean edgesEnabled;

    @ExceptionHandler(Exception.class)
    public void handleControllerException(Exception e, HttpServletResponse response) {
        ThingsboardException thingsboardException = handleException(e);
        if (thingsboardException.getErrorCode() == ThingsboardErrorCode.GENERAL && thingsboardException.getCause() instanceof Exception && StringUtils.equals(thingsboardException.getCause().getMessage(), thingsboardException.getMessage())) {
            e = (Exception) thingsboardException.getCause();
        } else {
            e = thingsboardException;
        }
        errorResponseHandler.handle(e, response);
    }

    @ExceptionHandler(ThingsboardException.class)
    public void handleThingsboardException(ThingsboardException ex, HttpServletResponse response) {
        errorResponseHandler.handle(ex, response);
    }

    /**
     * @deprecated Exceptions that are not of {@link ThingsboardException} type
     * are now caught and mapped to {@link ThingsboardException} by
     * {@link ExceptionHandler} {@link BaseController#handleControllerException(Exception, HttpServletResponse)}
     * which basically acts like the following boilerplate:
     * {@code
     * try {
     * someExceptionThrowingMethod();
     * } catch (Exception e) {
     * throw handleException(e);
     * }
     * }
     */
    @Deprecated
    ThingsboardException handleException(Exception exception) {
        return handleException(exception, true);
    }

    private ThingsboardException handleException(Exception exception, boolean logException) {
        if (logException && logControllerErrorStackTrace) {
            log.error("Error [{}]", exception.getMessage(), exception);
        }
        String cause = "";
        if (exception.getCause() != null) {
            cause = exception.getCause().getClass().getCanonicalName();
        }

        if (exception instanceof ThingsboardException) {
            return (ThingsboardException) exception;
        } else if (exception instanceof IllegalArgumentException || exception instanceof IncorrectParameterException || exception instanceof DataValidationException || cause.contains("IncorrectParameterException")) {
            return new ThingsboardException(exception.getMessage(), ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        } else if (exception instanceof MessagingException) {
            return new ThingsboardException("Unable to send mail: " + exception.getMessage(), ThingsboardErrorCode.GENERAL);
        } else if (exception instanceof AsyncRequestTimeoutException) {
            return new ThingsboardException("Request timeout", ThingsboardErrorCode.GENERAL);
        } else if (exception instanceof DataAccessException) {
            String errorType = exception.getClass().getSimpleName();
            if (!logControllerErrorStackTrace) { // not to log the error twice
                log.warn("Database error: {} - {}", errorType, ExceptionUtils.getRootCauseMessage(exception));
            }
            return new ThingsboardException("Database error", ThingsboardErrorCode.GENERAL);
        }
        return new ThingsboardException(exception.getMessage(), exception, ThingsboardErrorCode.GENERAL);
    }

    /**
     * Handles validation error for controller method arguments annotated with @{@link javax.validation.Valid}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationError(MethodArgumentNotValidException validationError, HttpServletResponse response) {
        List<ConstraintViolation<Object>> constraintsViolations = validationError.getFieldErrors().stream().map(fieldError -> {
            try {
                return (ConstraintViolation<Object>) fieldError.unwrap(ConstraintViolation.class);
            } catch (Exception e) {
                e.getMessage();
                return null; // should not happen
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        String errorMessage = "Validation error: " + ConstraintValidator.getErrorMessage(constraintsViolations);
        ThingsboardException thingsboardException = new ThingsboardException(errorMessage, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        handleControllerException(thingsboardException, response);
    }

    <T> T checkNotNull(T reference) throws ThingsboardException {
        return checkNotNull(reference, "Requested item wasn't found!");
    }

    <T> T checkNotNull(T reference, String notFoundMessage) throws ThingsboardException {
        if (reference == null) {
            throw new ThingsboardException(notFoundMessage, ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        return reference;
    }

    <T> T checkNotNull(Optional<T> reference) throws ThingsboardException {
        return checkNotNull(reference, "Requested item wasn't found!");
    }

    <T> T checkNotNull(Optional<T> reference, String notFoundMessage) throws ThingsboardException {
        if (reference.isPresent()) {
            return reference.get();
        } else {
            throw new ThingsboardException(notFoundMessage, ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
    }

    void checkParameter(String name, String param) throws ThingsboardException {
        if (StringUtils.isEmpty(param)) {
            throw new ThingsboardException("Parameter '" + name + "' can't be empty!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    void checkArrayParameter(String name, String[] params) throws ThingsboardException {
        if (params == null || params.length == 0) {
            throw new ThingsboardException("Parameter '" + name + "' can't be empty!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        } else {
            for (String param : params) {
                checkParameter(name, param);
            }
        }
    }

    protected <T> T checkEnumParameter(String name, String param, Function<String, T> valueOf) throws ThingsboardException {
        try {
            return valueOf.apply(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ThingsboardException(name + " \"" + param + "\" is not supported!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    UUID toUUID(String id) throws ThingsboardException {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw handleException(e, false);
        }
    }

    PageLink createPageLink(int pageSize, int page, String textSearch, String sortProperty, String sortOrder) throws ThingsboardException {
        if (StringUtils.isNotEmpty(sortProperty)) {
            if (!Validator.isValidProperty(sortProperty)) {
                throw new IllegalArgumentException("Invalid sort property");
            }
            SortOrder.Direction direction = SortOrder.Direction.ASC;
            if (StringUtils.isNotEmpty(sortOrder)) {
                try {
                    direction = SortOrder.Direction.valueOf(sortOrder.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ThingsboardException("Unsupported sort order '" + sortOrder + "'! Only 'ASC' or 'DESC' types are allowed.", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
                }
            }
            SortOrder sort = new SortOrder(sortProperty, direction);
            return new PageLink(pageSize, page, textSearch, sort);
        } else {
            return new PageLink(pageSize, page, textSearch);
        }
    }

    TimePageLink createTimePageLink(int pageSize, int page, String textSearch, String sortProperty, String sortOrder, Long startTime, Long endTime) throws ThingsboardException {
        PageLink pageLink = this.createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return new TimePageLink(pageLink, startTime, endTime);
    }

    protected SecurityUser getCurrentUser() throws ThingsboardException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return (SecurityUser) authentication.getPrincipal();
        } else {
            throw new ThingsboardException("You aren't authorized to perform this operation!", ThingsboardErrorCode.AUTHENTICATION);
        }
    }

    Tenant checkTenantId(TenantId tenantId, Operation operation) throws ThingsboardException {
        return checkEntityId(tenantId, (t, i) -> tenantService.findTenantById(tenantId), operation);
    }

    TenantInfo checkTenantInfoId(TenantId tenantId, Operation operation) throws ThingsboardException {
        return checkEntityId(tenantId, (t, i) -> tenantService.findTenantInfoById(tenantId), operation);
    }

    TenantProfile checkTenantProfileId(TenantProfileId tenantProfileId, Operation operation) throws ThingsboardException {
        try {
            validateId(tenantProfileId, "Incorrect tenantProfileId " + tenantProfileId);
            TenantProfile tenantProfile = tenantProfileService.findTenantProfileById(getTenantId(), tenantProfileId);
            checkNotNull(tenantProfile, "Tenant profile with id [" + tenantProfileId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.TENANT_PROFILE, operation);
            return tenantProfile;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected TenantId getTenantId() throws ThingsboardException {
        return getCurrentUser().getTenantId();
    }

    Customer checkCustomerId(CustomerId customerId, Operation operation) throws ThingsboardException {
        return checkEntityId(customerId, customerService::findCustomerById, operation);
    }

    User checkUserId(UserId userId, Operation operation) throws ThingsboardException {
        return checkEntityId(userId, userService::findUserById, operation);
    }

    protected <I extends EntityId, T extends HasTenantId> void checkEntity(I entityId, T entity, Resource resource) throws ThingsboardException {
        if (entityId == null) {
            accessControlService.checkPermission(getCurrentUser(), resource, Operation.CREATE, null, entity);
        } else {
            checkEntityId(entityId, Operation.WRITE);
        }
    }

    protected void checkEntityId(EntityId entityId, Operation operation) throws ThingsboardException {
        try {
            if (entityId == null) {
                throw new ThingsboardException("Parameter entityId can't be empty!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            validateId(entityId.getId(), "Incorrect entityId " + entityId);
            switch (entityId.getEntityType()) {
                case ALARM:
                    checkAlarmId(new AlarmId(entityId.getId()), operation);
                    return;
                case DEVICE:
                    checkDeviceId(new DeviceId(entityId.getId()), operation);
                    return;
                case DEVICE_PROFILE:
                    checkDeviceProfileId(new DeviceProfileId(entityId.getId()), operation);
                    return;
                case CUSTOMER:
                    checkCustomerId(new CustomerId(entityId.getId()), operation);
                    return;
                case TENANT:
                    checkTenantId(TenantId.fromUUID(entityId.getId()), operation);
                    return;
                case TENANT_PROFILE:
                    checkTenantProfileId(new TenantProfileId(entityId.getId()), operation);
                    return;
                case DRIVER:
                    checkDriverId(new DriverId(entityId.getId()), operation);
                    return;
                case RULE_CHAIN:
                    checkRuleChain(new RuleChainId(entityId.getId()), operation);
                    return;
                case RULE_NODE:
                    checkRuleNode(new RuleNodeId(entityId.getId()), operation);
                    return;
                case ASSET:
                    checkAssetId(new AssetId(entityId.getId()), operation);
                    return;
                case DATACONVERTER:
                    checkDataConverterId(new DataConverterId(entityId.getId()), operation);
                    return;
                case ROLES:
                    checkRolesId(new RolesId(entityId.getId()), operation);
                    return;
                case SCHEDULER:
                    checkSchedulerId(new SchedulerId(entityId.getId()), operation);
                    return;
                case ASSET_PROFILE:
                    checkAssetProfileId(new AssetProfileId(entityId.getId()), operation);
                    return;
                case INTEGRATION:
                    checkIntegrationId(new IntegrationId(entityId.getId()), operation);
                    return;
                case DASHBOARD:
                    checkDashboardId(new DashboardId(entityId.getId()), operation);
                    return;
                case REPORT:
                    checkReportId(new ReportId(entityId.getId()), operation);
                    return;
                case TRIP:
                    checkTripId(new TripId(entityId.getId()), operation);
                    return;
                case USER:
                    checkUserId(new UserId(entityId.getId()), operation);
                    return;
                case ENTITY_VIEW:
                    checkEntityViewId(new EntityViewId(entityId.getId()), operation);
                    return;
                case EDGE:
                    checkEdgeId(new EdgeId(entityId.getId()), operation);
                    return;
                case WIDGETS_BUNDLE:
                    checkWidgetsBundleId(new WidgetsBundleId(entityId.getId()), operation);
                    return;
                case WIDGET_TYPE:
                    checkWidgetTypeId(new WidgetTypeId(entityId.getId()), operation);
                    return;
                case TB_RESOURCE:
                    checkResourceInfoId(new TbResourceId(entityId.getId()), operation);
                    return;
                case OTA_PACKAGE:
                    checkOtaPackageId(new OtaPackageId(entityId.getId()), operation);
                    return;
                case QUEUE:
                    checkQueueId(new QueueId(entityId.getId()), operation);
                    return;
                case VEHICLE:
                    checkVehicleId(new VehicleId(entityId.getId()), operation);
                    return;
                default:
                    checkEntityId(entityId, entitiesService::findEntityByTenantIdAndId, operation);
            }
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected <E extends HasId<I> & HasTenantId, I extends EntityId> E checkEntityId(I entityId, ThrowingBiFunction<TenantId, I, E> findingFunction, Operation operation) throws ThingsboardException {
        try {
            validateId((UUIDBased) entityId, "Invalid entity id");
            SecurityUser user = getCurrentUser();
            E entity = findingFunction.apply(user.getTenantId(), entityId);
            checkNotNull(entity, entityId.getEntityType().getNormalName() + " with id [" + entityId + "] is not found");
            return checkEntity(user, entity, operation);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected <E extends HasId<I> & HasTenantId, I extends EntityId> E checkEntity(SecurityUser user, E entity, Operation operation) throws ThingsboardException {
        checkNotNull(entity, "Entity not found");
        accessControlService.checkPermission(user, Resource.of(entity.getId().getEntityType()), operation, entity.getId(), entity);
        return entity;
    }

    Device checkDeviceId(DeviceId deviceId, Operation operation) throws ThingsboardException {
        return checkEntityId(deviceId, deviceService::findDeviceById, operation);
    }

    DeviceInfo checkDeviceInfoId(DeviceId deviceId, Operation operation) throws ThingsboardException {
        return checkEntityId(deviceId, deviceService::findDeviceInfoById, operation);
    }

    List<String> findAllIdByDeviceType(String deviceType) throws ThingsboardException {
        return deviceService.findAllIdByDeviceType(deviceType);
    }

    DeviceProfile checkDeviceProfileId(DeviceProfileId deviceProfileId, Operation operation) throws ThingsboardException {
        return checkEntityId(deviceProfileId, deviceProfileService::findDeviceProfileById, operation);
    }

    protected EntityView checkEntityViewId(EntityViewId entityViewId, Operation operation) throws ThingsboardException {
        return checkEntityId(entityViewId, entityViewService::findEntityViewById, operation);
    }

    EntityViewInfo checkEntityViewInfoId(EntityViewId entityViewId, Operation operation) throws ThingsboardException {
        return checkEntityId(entityViewId, entityViewService::findEntityViewInfoById, operation);
    }

    Integration checkIntegrationId(IntegrationId integrationId, Operation operation) throws ThingsboardException {
        return checkEntityId(integrationId, integrationService::findIntegrationById, operation);
    }

    IntegrationInfo checkIntegrationInfoId(IntegrationId integrationId, Operation operation) throws ThingsboardException {
        return checkEntityId(integrationId, integrationService::findIntegrationInfoById, operation);
    }

    Asset checkAssetId(AssetId assetId, Operation operation) throws ThingsboardException {
        return checkEntityId(assetId, assetService::findAssetById, operation);
    }

    DataConverter checkDataConverterId(DataConverterId dataConverterId, Operation operation) throws ThingsboardException {
        return checkEntityId(dataConverterId, dataConverterService::findDataConverterById, operation);
    }

    AssetInfo checkAssetInfoId(AssetId assetId, Operation operation) throws ThingsboardException {
        return checkEntityId(assetId, assetService::findAssetInfoById, operation);
    }

    DataConverterInfo checkDataConverterInfoId(DataConverterId dataConverterId, Operation operation) throws ThingsboardException {
        return checkEntityId(dataConverterId, dataConverterService::findDataConverterInfoById, operation);
    }


    Roles checkRolesId(RolesId rolesId, Operation operation) throws ThingsboardException {
        return checkEntityId(rolesId, rolesService::findRolesById, operation);
    }

    RolesInfo checkRolesInfoId(RolesId rolesId, Operation operation) throws ThingsboardException {
        return checkEntityId(rolesId, rolesService::findRolesInfoById, operation);
    }

    Vehicle checkVehicleId(VehicleId vehicleId, Operation operation) throws ThingsboardException {
        return checkEntityId(vehicleId, vehicleService::findVehicleById, operation);
    }

    AssetProfile checkAssetProfileId(AssetProfileId assetProfileId, Operation operation) throws ThingsboardException {
        return checkEntityId(assetProfileId, assetProfileService::findAssetProfileById, operation);
    }

    Scheduler checkSchedulerId(SchedulerId schedulerId, Operation operation) throws ThingsboardException {
        return checkEntityId(schedulerId, schedulerService::findSchedulerById, operation);
    }

    SchedulerInfo checkSchedulerInfoId(SchedulerId schedulerId, Operation operation) throws ThingsboardException {
        return checkEntityId(schedulerId, schedulerService::findSchedulerInfoById, operation);
    }

    Driver checkDriverId(DriverId driverId, Operation operation) throws ThingsboardException {
        return checkEntityId(driverId, driverService::findDriverById, operation);
    }

    Alarm checkAlarmId(AlarmId alarmId, Operation operation) throws ThingsboardException {
        return checkEntityId(alarmId, alarmService::findAlarmById, operation);
    }

    AlarmInfo checkAlarmInfoId(AlarmId alarmId, Operation operation) throws ThingsboardException {
        return checkEntityId(alarmId, alarmService::findAlarmInfoById, operation);
    }

    AlarmComment checkAlarmCommentId(AlarmCommentId alarmCommentId, AlarmId alarmId) throws ThingsboardException {
        try {
            validateId(alarmCommentId, "Incorrect alarmCommentId " + alarmCommentId);
            AlarmComment alarmComment = alarmCommentService.findAlarmCommentByIdAsync(getCurrentUser().getTenantId(), alarmCommentId).get();
            checkNotNull(alarmComment, "Alarm comment with id [" + alarmCommentId + "] is not found");
            if (!alarmId.equals(alarmComment.getAlarmId())) {
                throw new ThingsboardException("Alarm id does not match with comment alarm id", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            return alarmComment;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    WidgetsBundle checkWidgetsBundleId(WidgetsBundleId widgetsBundleId, Operation operation) throws ThingsboardException {
        return checkEntityId(widgetsBundleId, widgetsBundleService::findWidgetsBundleById, operation);
    }

    WidgetTypeDetails checkWidgetTypeId(WidgetTypeId widgetTypeId, Operation operation) throws ThingsboardException {
        return checkEntityId(widgetTypeId, widgetTypeService::findWidgetTypeDetailsById, operation);
    }

    Dashboard checkDashboardId(DashboardId dashboardId, Operation operation) throws ThingsboardException {
        return checkEntityId(dashboardId, dashboardService::findDashboardById, operation);
    }

    Report checkReportId(ReportId reportId, Operation operation) throws ThingsboardException {
        return checkEntityId(reportId, reportService::findReportById, operation);
    }

    ReportInfo checkReportInfoId(ReportId reportId, Operation operation) throws ThingsboardException {
        return checkEntityId(reportId, reportService::findReportById, operation);
    }

    Trip checkTripId(TripId tripId, Operation operation) throws ThingsboardException {
        return checkEntityId(tripId, tripService::findTripById, operation);
    }

    Edge checkEdgeId(EdgeId edgeId, Operation operation) throws ThingsboardException {
        return checkEntityId(edgeId, edgeService::findEdgeById, operation);
    }

    EdgeInfo checkEdgeInfoId(EdgeId edgeId, Operation operation) throws ThingsboardException {
        return checkEntityId(edgeId, edgeService::findEdgeInfoById, operation);
    }

    DashboardInfo checkDashboardInfoId(DashboardId dashboardId, Operation operation) throws ThingsboardException {
        return checkEntityId(dashboardId, dashboardService::findDashboardInfoById, operation);
    }

    ComponentDescriptor checkComponentDescriptorByClazz(String clazz) throws ThingsboardException {
        try {
            return checkNotNull(componentDescriptorService.getComponent(clazz));
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<ComponentDescriptor> checkComponentDescriptorsByType(ComponentType type, RuleChainType ruleChainType) throws ThingsboardException {
        try {
            return componentDescriptorService.getComponents(type, ruleChainType);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<ComponentDescriptor> checkComponentDescriptorsByTypes(Set<ComponentType> types, RuleChainType ruleChainType) throws ThingsboardException {
        try {
            return componentDescriptorService.getComponents(types, ruleChainType);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected RuleChain checkRuleChain(RuleChainId ruleChainId, Operation operation) throws ThingsboardException {
        return checkEntityId(ruleChainId, ruleChainService::findRuleChainById, operation);
    }

    protected RuleNode checkRuleNode(RuleNodeId ruleNodeId, Operation operation) throws ThingsboardException {
        validateId(ruleNodeId, "Incorrect ruleNodeId " + ruleNodeId);
        RuleNode ruleNode = ruleChainService.findRuleNodeById(getTenantId(), ruleNodeId);
        checkNotNull(ruleNode, "Rule node with id [" + ruleNodeId + "] is not found");
        checkRuleChain(ruleNode.getRuleChainId(), operation);
        return ruleNode;
    }

    TbResource checkResourceId(TbResourceId resourceId, Operation operation) throws ThingsboardException {
        return checkEntityId(resourceId, resourceService::findResourceById, operation);
    }

    TbResourceInfo checkResourceInfoId(TbResourceId resourceId, Operation operation) throws ThingsboardException {
        return checkEntityId(resourceId, resourceService::findResourceInfoById, operation);
    }

    OtaPackage checkOtaPackageId(OtaPackageId otaPackageId, Operation operation) throws ThingsboardException {
        return checkEntityId(otaPackageId, otaPackageService::findOtaPackageById, operation);
    }

    OtaPackageInfo checkOtaPackageInfoId(OtaPackageId otaPackageId, Operation operation) throws ThingsboardException {
        return checkEntityId(otaPackageId, otaPackageService::findOtaPackageInfoById, operation);
    }

    Rpc checkRpcId(RpcId rpcId, Operation operation) throws ThingsboardException {
        return checkEntityId(rpcId, rpcService::findById, operation);
    }


    Roles checkRoles(RolesId rolesId, Operation operation) throws ThingsboardException {
        return checkEntityId(rolesId, rolesService::findRolesById, operation);
    }

    protected Queue checkQueueId(QueueId queueId, Operation operation) throws ThingsboardException {
        Queue queue = checkEntityId(queueId, queueService::findQueueById, operation);
        TenantId tenantId = getTenantId();
        if (queue.getTenantId().isNullUid() && !tenantId.isNullUid()) {
            TenantProfile tenantProfile = tenantProfileCache.get(tenantId);
            if (tenantProfile.isIsolatedTbRuleEngine()) {
                throw new ThingsboardException(YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION, ThingsboardErrorCode.PERMISSION_DENIED);
            }
        }
        return queue;
    }

    protected <I extends EntityId> I emptyId(EntityType entityType) {
        return (I) EntityIdFactory.getByTypeAndUuid(entityType, ModelConstants.NULL_UUID);
    }

    public static Exception toException(Throwable error) {
        return error != null ? (Exception.class.isInstance(error) ? (Exception) error : new Exception(error)) : null;
    }

    protected <E extends HasName & HasId<? extends EntityId>> void logEntityAction(SecurityUser user, EntityType entityType, E savedEntity, ActionType actionType) {
        logEntityAction(user, entityType, null, savedEntity, actionType, null);
    }

    protected <E extends HasName & HasId<? extends EntityId>> void logEntityAction(SecurityUser user, EntityType entityType, E entity, E savedEntity, ActionType actionType, Exception e) {
        EntityId entityId = savedEntity != null ? savedEntity.getId() : emptyId(entityType);
        if (!user.isSystemAdmin()) {
            entityActionService.logEntityAction(user, entityId, savedEntity != null ? savedEntity : entity, user.getCustomerId(), actionType, e);
        }
    }

    protected <E extends HasName & HasId<? extends EntityId>> E doSaveAndLog(EntityType entityType, E entity, BiFunction<TenantId, E, E> savingFunction) throws Exception {
        ActionType actionType = entity.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        SecurityUser user = getCurrentUser();
        try {
            E savedEntity = savingFunction.apply(user.getTenantId(), entity);
            logEntityAction(user, entityType, savedEntity, actionType);
            return savedEntity;
        } catch (Exception e) {
            logEntityAction(user, entityType, entity, null, actionType, e);
            throw e;
        }
    }

    protected <E extends HasName & HasId<I>, I extends EntityId> void doDeleteAndLog(EntityType entityType, E entity, BiConsumer<TenantId, I> deleteFunction) throws Exception {
        SecurityUser user = getCurrentUser();
        try {
            deleteFunction.accept(user.getTenantId(), entity.getId());
            logEntityAction(user, entityType, entity, ActionType.DELETED);
        } catch (Exception e) {
            logEntityAction(user, entityType, entity, entity, ActionType.DELETED, e);
            throw e;
        }
    }

    protected void processDashboardIdFromAdditionalInfo(ObjectNode additionalInfo, String requiredFields) throws ThingsboardException {
        String dashboardId = additionalInfo.has(requiredFields) ? additionalInfo.get(requiredFields).asText() : null;
        if (dashboardId != null && !dashboardId.equals("null")) {
            if (dashboardService.findDashboardById(getTenantId(), new DashboardId(UUID.fromString(dashboardId))) == null) {
                additionalInfo.remove(requiredFields);
            }
        }
    }

    protected MediaType parseMediaType(String contentType) {
        try {
            return MediaType.parseMediaType(contentType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    protected <T> DeferredResult<T> wrapFuture(ListenableFuture<T> future) {
        DeferredResult<T> deferredResult = new DeferredResult<>(); // Timeout of spring.mvc.async.request-timeout is used
        DonAsynchron.withCallback(future, deferredResult::setResult, deferredResult::setErrorResult);
        return deferredResult;
    }

    protected <T> DeferredResult<T> wrapFuture(ListenableFuture<T> future, long timeoutMs) {
        DeferredResult<T> deferredResult = new DeferredResult<>(timeoutMs);
        DonAsynchron.withCallback(future, deferredResult::setResult, deferredResult::setErrorResult);
        return deferredResult;
    }

    protected EntityDataSortOrder createEntityDataSortOrder(String sortProperty, String sortOrder) {
        if (isNotEmpty(sortProperty)) {
            EntityDataSortOrder entityDataSortOrder = new EntityDataSortOrder();
            entityDataSortOrder.setKey(new EntityKey(ENTITY_FIELD, sortProperty));
            if (isNotEmpty(sortOrder)) {
                entityDataSortOrder.setDirection(EntityDataSortOrder.Direction.valueOf(sortOrder));
            }
            return entityDataSortOrder;
        } else {
            return null;
        }
    }

}
