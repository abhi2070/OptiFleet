
package org.thingsboard.server.common.data;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public enum EntityType {
    TENANT(1),
    CUSTOMER(2),
    USER(3),
    DASHBOARD(4),
    ASSET(5),
    DEVICE(6),
    DATACONVERTER(9),
    ALARM (7),
    INTEGRATION (10),
    ROLES (8),
    RULE_CHAIN (11),
    RULE_NODE (12),
    SCHEDULER(13),
    REPORT(35),
    DRIVER(37),
    TRIP(36),

    ENTITY_VIEW (15) {
        // backward compatibility for TbOriginatorTypeSwitchNode to return correct rule node connection.
        @Override
        public String getNormalName () {
            return "Entity View";
        }
    },
    WIDGETS_BUNDLE (16),
    WIDGET_TYPE (17),
    TENANT_PROFILE (20),
    DEVICE_PROFILE (21),
    ASSET_PROFILE (22),
    API_USAGE_STATE (23),
    TB_RESOURCE (24),
    OTA_PACKAGE (25),
    EDGE (26),
    RPC (27),
    QUEUE (28),
    NOTIFICATION_TARGET (29),
    NOTIFICATION_TEMPLATE (30),
    NOTIFICATION_REQUEST (31),
    NOTIFICATION (32),
    NOTIFICATION_RULE (33),
    VEHICLE (34);

    @Getter
    private final int protoNumber; // Corresponds to EntityTypeProto

    private EntityType(int protoNumber) {
        this.protoNumber = protoNumber;
    }

    public static final List<String> NORMAL_NAMES = EnumSet.allOf(EntityType.class).stream()
            .map(EntityType::getNormalName).collect(Collectors.toUnmodifiableList());

    @Getter
    private final String normalName = StringUtils.capitalize(StringUtils.removeStart(name(), "TB_")
            .toLowerCase().replaceAll("_", " "));

}
