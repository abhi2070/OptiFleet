
package org.thingsboard.server.service.security.permission;

import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.service.security.model.SecurityUser;

@Component(value="tenantAdminPermissions")
public class TenantAdminPermissions extends AbstractPermissions {

    public TenantAdminPermissions() {
        super();
        put(Resource.ADMIN_SETTINGS, PermissionChecker.allowAllPermissionChecker);
        put(Resource.ALARM, tenantEntityPermissionChecker);
        put(Resource.ASSET, tenantEntityPermissionChecker);
        put(Resource.INTEGRATION, tenantEntityPermissionChecker);
        put(Resource.DATACONVERTER, tenantEntityPermissionChecker);
        put(Resource.DEVICE, tenantEntityPermissionChecker);
        put(Resource.ROLES, tenantEntityPermissionChecker);
        put(Resource.VEHICLE, tenantEntityPermissionChecker);
        put(Resource.SCHEDULER, tenantEntityPermissionChecker);
        put(Resource.TRIP, tenantEntityPermissionChecker);
        put(Resource.CUSTOMER, tenantEntityPermissionChecker);
        put(Resource.DASHBOARD, tenantEntityPermissionChecker);
        put(Resource.REPORT, tenantEntityPermissionChecker);
        put(Resource.DRIVER, tenantEntityPermissionChecker);
        put(Resource.ENTITY_VIEW, tenantEntityPermissionChecker);
        put(Resource.TENANT, tenantPermissionChecker);
        put(Resource.RULE_CHAIN, tenantEntityPermissionChecker);
        put(Resource.USER, userPermissionChecker);
        put(Resource.WIDGETS_BUNDLE, widgetsPermissionChecker);
        put(Resource.WIDGET_TYPE, widgetsPermissionChecker);
        put(Resource.DEVICE_PROFILE, tenantEntityPermissionChecker);
        put(Resource.ASSET_PROFILE, tenantEntityPermissionChecker);
        put(Resource.API_USAGE_STATE, tenantEntityPermissionChecker);
        put(Resource.TB_RESOURCE, tbResourcePermissionChecker);
        put(Resource.OTA_PACKAGE, tenantEntityPermissionChecker);
        put(Resource.EDGE, tenantEntityPermissionChecker);
        put(Resource.RPC, tenantEntityPermissionChecker);
        put(Resource.QUEUE, queuePermissionChecker);
        put(Resource.VERSION_CONTROL, PermissionChecker.allowAllPermissionChecker);
        put(Resource.NOTIFICATION, tenantEntityPermissionChecker);
    }

    public static final PermissionChecker tenantEntityPermissionChecker = new PermissionChecker() {

        @Override
        public boolean hasPermission(SecurityUser user, Operation operation, EntityId entityId, HasTenantId entity) {

            if (!user.getTenantId().equals(entity.getTenantId())) {
                return false;
            }
            return true;
        }
    };

    private static final PermissionChecker tenantPermissionChecker =
            new PermissionChecker.GenericPermissionChecker(Operation.READ, Operation.READ_ATTRIBUTES, Operation.READ_TELEMETRY) {

                @Override
                @SuppressWarnings("unchecked")
                public boolean hasPermission(SecurityUser user, Operation operation, EntityId entityId, HasTenantId entity) {
                    if (!super.hasPermission(user, operation, entityId, entity)) {
                        return false;
                    }
                    if (!user.getTenantId().equals(entityId)) {
                        return false;
                    }
                    return true;
                }

            };

    private static final PermissionChecker userPermissionChecker = new PermissionChecker<UserId, User>() {

        @Override
        public boolean hasPermission(SecurityUser user, Operation operation, UserId userId, User userEntity) {
            if (Authority.SYS_ADMIN.equals(userEntity.getAuthority())) {
                return false;
            }
            if (!user.getTenantId().equals(userEntity.getTenantId())) {
                return false;
            }
            return true;
        }

    };

    private static final PermissionChecker widgetsPermissionChecker = new PermissionChecker() {

        @Override
        public boolean hasPermission(SecurityUser user, Operation operation, EntityId entityId, HasTenantId entity) {
            if (entity.getTenantId() == null || entity.getTenantId().isNullUid()) {
                return operation == Operation.READ;
            }
            if (!user.getTenantId().equals(entity.getTenantId())) {
                return false;
            }
            return true;
        }

    };

    private static final PermissionChecker tbResourcePermissionChecker = new PermissionChecker() {

        @Override
        public boolean hasPermission(SecurityUser user, Operation operation, EntityId entityId, HasTenantId entity) {
            if (entity.getTenantId() == null || entity.getTenantId().isNullUid()) {
                return operation == Operation.READ;
            }
            if (!user.getTenantId().equals(entity.getTenantId())) {
                return false;
            }
            return true;
        }

    };

    private static final PermissionChecker queuePermissionChecker = new PermissionChecker() {

        @Override
        public boolean hasPermission(SecurityUser user, Operation operation, EntityId entityId, HasTenantId entity) {
            if (entity.getTenantId() == null || entity.getTenantId().isNullUid()) {
                return operation == Operation.READ;
            }
            if (!user.getTenantId().equals(entity.getTenantId())) {
                return false;
            }
            return true;
        }

    };

}
