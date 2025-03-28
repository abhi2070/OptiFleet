
package org.thingsboard.server.dao.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.CacheConstants;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.notification.settings.NotificationSettings;
import org.thingsboard.server.common.data.notification.settings.UserNotificationSettings;
import org.thingsboard.server.common.data.notification.settings.UserNotificationSettings.NotificationPref;
import org.thingsboard.server.common.data.notification.targets.NotificationTarget;
import org.thingsboard.server.common.data.notification.targets.platform.AffectedTenantAdministratorsFilter;
import org.thingsboard.server.common.data.notification.targets.platform.AffectedUserFilter;
import org.thingsboard.server.common.data.notification.targets.platform.AllUsersFilter;
import org.thingsboard.server.common.data.notification.targets.platform.OriginatorEntityOwnerUsersFilter;
import org.thingsboard.server.common.data.notification.targets.platform.PlatformUsersNotificationTargetConfig;
import org.thingsboard.server.common.data.notification.targets.platform.SystemAdministratorsFilter;
import org.thingsboard.server.common.data.notification.targets.platform.TenantAdministratorsFilter;
import org.thingsboard.server.common.data.notification.targets.platform.UsersFilter;
import org.thingsboard.server.common.data.notification.targets.platform.UsersFilterType;
import org.thingsboard.server.common.data.notification.template.NotificationTemplate;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.settings.UserSettings;
import org.thingsboard.server.common.data.settings.UserSettingsType;
import org.thingsboard.server.dao.settings.AdminSettingsService;
import org.thingsboard.server.dao.user.UserSettingsService;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultNotificationSettingsService implements NotificationSettingsService {

    private final AdminSettingsService adminSettingsService;
    private final NotificationTargetService notificationTargetService;
    private final NotificationTemplateService notificationTemplateService;
    private final DefaultNotifications defaultNotifications;
    private final UserSettingsService userSettingsService;

    private static final String SETTINGS_KEY = "notifications";

    @CacheEvict(cacheNames = CacheConstants.NOTIFICATION_SETTINGS_CACHE, key = "#tenantId")
    @Override
    public void saveNotificationSettings(TenantId tenantId, NotificationSettings settings) {
        if (!tenantId.isSysTenantId() && settings.getDeliveryMethodsConfigs().containsKey(NotificationDeliveryMethod.MOBILE_APP)) {
            throw new IllegalArgumentException("Mobile settings can only be configured by system administrator");
        }
        AdminSettings adminSettings = Optional.ofNullable(adminSettingsService.findAdminSettingsByTenantIdAndKey(tenantId, SETTINGS_KEY))
                .orElseGet(() -> {
                    AdminSettings newAdminSettings = new AdminSettings();
                    newAdminSettings.setTenantId(tenantId);
                    newAdminSettings.setKey(SETTINGS_KEY);
                    return newAdminSettings;
                });
        adminSettings.setJsonValue(JacksonUtil.valueToTree(settings));
        adminSettingsService.saveAdminSettings(tenantId, adminSettings);
    }

    @Cacheable(cacheNames = CacheConstants.NOTIFICATION_SETTINGS_CACHE, key = "#tenantId")
    @Override
    public NotificationSettings findNotificationSettings(TenantId tenantId) {
        return Optional.ofNullable(adminSettingsService.findAdminSettingsByTenantIdAndKey(tenantId, SETTINGS_KEY))
                .map(adminSettings -> JacksonUtil.treeToValue(adminSettings.getJsonValue(), NotificationSettings.class))
                .orElseGet(() -> {
                    NotificationSettings settings = new NotificationSettings();
                    settings.setDeliveryMethodsConfigs(Collections.emptyMap());
                    return settings;
                });
    }

    @CacheEvict(cacheNames = CacheConstants.NOTIFICATION_SETTINGS_CACHE, key = "#tenantId")
    @Override
    public void deleteNotificationSettings(TenantId tenantId) {
        adminSettingsService.deleteAdminSettingsByTenantIdAndKey(tenantId, SETTINGS_KEY);
    }

    @Override
    public UserNotificationSettings saveUserNotificationSettings(TenantId tenantId, UserId userId, UserNotificationSettings settings) {
        UserSettings userSettings = new UserSettings();
        userSettings.setUserId(userId);
        userSettings.setType(UserSettingsType.NOTIFICATIONS);
        userSettings.setSettings(JacksonUtil.valueToTree(settings));
        userSettingsService.saveUserSettings(tenantId, userSettings);
        return formatUserNotificationSettings(settings);
    }

    @Override
    public UserNotificationSettings getUserNotificationSettings(TenantId tenantId, UserId userId, boolean format) {
        UserSettings userSettings = userSettingsService.findUserSettings(tenantId, userId, UserSettingsType.NOTIFICATIONS);
        UserNotificationSettings settings = null;
        if (userSettings != null) {
            try {
                settings = JacksonUtil.treeToValue(userSettings.getSettings(), UserNotificationSettings.class);
            } catch (Exception e) {
                log.warn("Failed to parse notification settings for user {}", userId, e);
            }
        }
        if (settings == null) {
            settings = UserNotificationSettings.DEFAULT;
        }
        if (format) {
            settings = formatUserNotificationSettings(settings);
        }
        return settings;
    }

    private UserNotificationSettings formatUserNotificationSettings(UserNotificationSettings settings) {
        Map<NotificationType, NotificationPref> prefs = new EnumMap<>(NotificationType.class);
        if (settings != null) {
            prefs.putAll(settings.getPrefs());
        }
        NotificationPref defaultPref = NotificationPref.createDefault();
        for (NotificationType notificationType : NotificationType.values()) {
            NotificationPref pref = prefs.get(notificationType);
            if (pref == null) {
                prefs.put(notificationType, defaultPref);
            } else {
                var enabledDeliveryMethods = new LinkedHashMap<>(pref.getEnabledDeliveryMethods());
                // in case a new delivery method was added to the platform
                UserNotificationSettings.deliveryMethods.forEach(deliveryMethod -> {
                    enabledDeliveryMethods.putIfAbsent(deliveryMethod, true);
                });
                pref.setEnabledDeliveryMethods(enabledDeliveryMethods);
            }
        }
        return new UserNotificationSettings(prefs);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED) // so that parent transaction is not aborted on method failure
    @Override
    public void createDefaultNotificationConfigs(TenantId tenantId) {
        NotificationTarget allUsers = createTarget(tenantId, "All users", new AllUsersFilter(),
                tenantId.isSysTenantId() ? "All platform users" : "All users in scope of the tenant");
        NotificationTarget tenantAdmins = createTarget(tenantId, "Tenant administrators", new TenantAdministratorsFilter(),
                tenantId.isSysTenantId() ? "All tenant administrators" : "Tenant administrators");

        defaultNotifications.create(tenantId, DefaultNotifications.maintenanceWork);

        if (tenantId.isSysTenantId()) {
            NotificationTarget sysAdmins = createTarget(tenantId, "System administrators", new SystemAdministratorsFilter(), "All system administrators");
            NotificationTarget affectedTenantAdmins = createTarget(tenantId, "Affected tenant's administrators", new AffectedTenantAdministratorsFilter(), "");

            defaultNotifications.create(tenantId, DefaultNotifications.entitiesLimitForSysadmin, sysAdmins.getId());
            defaultNotifications.create(tenantId, DefaultNotifications.entitiesLimitForTenant, affectedTenantAdmins.getId());

            defaultNotifications.create(tenantId, DefaultNotifications.apiFeatureWarningForSysadmin, sysAdmins.getId());
            defaultNotifications.create(tenantId, DefaultNotifications.apiFeatureWarningForTenant, affectedTenantAdmins.getId());
            defaultNotifications.create(tenantId, DefaultNotifications.apiFeatureDisabledForSysadmin, sysAdmins.getId());
            defaultNotifications.create(tenantId, DefaultNotifications.apiFeatureDisabledForTenant, affectedTenantAdmins.getId());

            defaultNotifications.create(tenantId, DefaultNotifications.exceededRateLimits, affectedTenantAdmins.getId());
            defaultNotifications.create(tenantId, DefaultNotifications.exceededPerEntityRateLimits, affectedTenantAdmins.getId());
            defaultNotifications.create(tenantId, DefaultNotifications.exceededRateLimitsForSysadmin, sysAdmins.getId());

            defaultNotifications.create(tenantId, DefaultNotifications.newPlatformVersion, sysAdmins.getId());
            return;
        }

        NotificationTarget originatorEntityOwnerUsers = createTarget(tenantId, "Users of the entity owner", new OriginatorEntityOwnerUsersFilter(),
                "In case trigger entity (e.g. created device or alarm) is owned by customer, then recipients are this customer's users, otherwise tenant admins");
        NotificationTarget affectedUser = createTarget(tenantId, "Affected user", new AffectedUserFilter(),
                "If rule trigger is an action that affects some user (e.g. alarm assigned to user) - this user");

        defaultNotifications.create(tenantId, DefaultNotifications.newAlarm, tenantAdmins.getId());
        defaultNotifications.create(tenantId, DefaultNotifications.alarmUpdate, tenantAdmins.getId());
        defaultNotifications.create(tenantId, DefaultNotifications.entityAction, tenantAdmins.getId());
        defaultNotifications.create(tenantId, DefaultNotifications.deviceActivity, tenantAdmins.getId());
        defaultNotifications.create(tenantId, DefaultNotifications.alarmComment, tenantAdmins.getId());
        defaultNotifications.create(tenantId, DefaultNotifications.alarmAssignment, affectedUser.getId());
        defaultNotifications.create(tenantId, DefaultNotifications.ruleEngineComponentLifecycleFailure, tenantAdmins.getId());
        defaultNotifications.create(tenantId, DefaultNotifications.edgeConnection, tenantAdmins.getId());
        defaultNotifications.create(tenantId, DefaultNotifications.edgeCommunicationFailures, tenantAdmins.getId());
    }

    @Override
    public void updateDefaultNotificationConfigs(TenantId tenantId) {
        if (tenantId.isSysTenantId()) {
            if (notificationTemplateService.findNotificationTemplatesByTenantIdAndNotificationTypes(tenantId,
                    List.of(NotificationType.RATE_LIMITS), new PageLink(1)).getTotalElements() > 0) {
                return;
            }

            NotificationTarget sysAdmins = notificationTargetService.findNotificationTargetsByTenantIdAndUsersFilterType(tenantId, UsersFilterType.SYSTEM_ADMINISTRATORS).stream()
                    .findFirst().orElseGet(() -> createTarget(tenantId, "System administrators", new SystemAdministratorsFilter(), "All system administrators"));
            NotificationTarget affectedTenantAdmins = notificationTargetService.findNotificationTargetsByTenantIdAndUsersFilterType(tenantId, UsersFilterType.AFFECTED_TENANT_ADMINISTRATORS).stream()
                    .findFirst().orElseGet(() -> createTarget(tenantId, "Affected tenant's administrators", new AffectedTenantAdministratorsFilter(), ""));

            defaultNotifications.create(tenantId, DefaultNotifications.exceededRateLimits, affectedTenantAdmins.getId());
            defaultNotifications.create(tenantId, DefaultNotifications.exceededPerEntityRateLimits, affectedTenantAdmins.getId());
            defaultNotifications.create(tenantId, DefaultNotifications.exceededRateLimitsForSysadmin, sysAdmins.getId());
        } else {
            var requiredNotificationTypes = List.of(NotificationType.EDGE_CONNECTION, NotificationType.EDGE_COMMUNICATION_FAILURE);
            var existingNotificationTypes = notificationTemplateService.findNotificationTemplatesByTenantIdAndNotificationTypes(
                            tenantId, requiredNotificationTypes, new PageLink(1))
                    .getData()
                    .stream()
                    .map(NotificationTemplate::getNotificationType)
                    .collect(Collectors.toSet());

            if (existingNotificationTypes.containsAll(requiredNotificationTypes)) {
                return;
            }

            NotificationTarget tenantAdmins = notificationTargetService.findNotificationTargetsByTenantIdAndUsersFilterType(tenantId, UsersFilterType.TENANT_ADMINISTRATORS)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> createTarget(tenantId, "Tenant administrators", new TenantAdministratorsFilter(), "Tenant administrators"));

            for (NotificationType type : requiredNotificationTypes) {
                if (!existingNotificationTypes.contains(type)) {
                    switch (type) {
                        case EDGE_CONNECTION:
                            defaultNotifications.create(tenantId, DefaultNotifications.edgeConnection, tenantAdmins.getId());
                            break;
                        case EDGE_COMMUNICATION_FAILURE:
                            defaultNotifications.create(tenantId, DefaultNotifications.edgeCommunicationFailures, tenantAdmins.getId());
                            break;
                    }
                }
            }
        }
    }

    private NotificationTarget createTarget(TenantId tenantId, String name, UsersFilter filter, String description) {
        NotificationTarget target = new NotificationTarget();
        target.setTenantId(tenantId);
        target.setName(name);

        PlatformUsersNotificationTargetConfig targetConfig = new PlatformUsersNotificationTargetConfig();
        targetConfig.setUsersFilter(filter);
        targetConfig.setDescription(description);
        target.setConfiguration(targetConfig);
        return notificationTargetService.saveNotificationTarget(tenantId, target);
    }

}
