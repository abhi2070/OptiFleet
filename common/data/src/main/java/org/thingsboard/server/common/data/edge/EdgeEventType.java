
package org.thingsboard.server.common.data.edge;

import lombok.Getter;
import org.thingsboard.server.common.data.EntityType;

@Getter
public enum EdgeEventType {
    DASHBOARD(false, EntityType.DASHBOARD),
    ASSET(false, EntityType.ASSET),
    DATACONVERTER(false, EntityType.DATACONVERTER),
    INTEGRATION(false, EntityType.INTEGRATION),
    ROLES(false, EntityType.ROLES),
    VEHICLE(false, EntityType.VEHICLE),
    DEVICE(false, EntityType.DEVICE),
    DEVICE_PROFILE(true, EntityType.DEVICE_PROFILE),
    ASSET_PROFILE(true, EntityType.ASSET_PROFILE),
    ENTITY_VIEW(false, EntityType.ENTITY_VIEW),
    ALARM(false, EntityType.ALARM),
    ALARM_COMMENT(false, null),
    RULE_CHAIN(false, EntityType.RULE_CHAIN),
    RULE_CHAIN_METADATA(false, null),
    EDGE(false, EntityType.EDGE),
    USER(true, EntityType.USER),
    CUSTOMER(true, EntityType.CUSTOMER),
    RELATION(true, null),
    TENANT(true, EntityType.TENANT),
    TENANT_PROFILE(true, EntityType.TENANT_PROFILE),
    WIDGETS_BUNDLE(true, EntityType.WIDGETS_BUNDLE),
    WIDGET_TYPE(true, EntityType.WIDGET_TYPE),
    ADMIN_SETTINGS(true, null),
    OTA_PACKAGE(true, EntityType.OTA_PACKAGE),
    QUEUE(true, EntityType.QUEUE),
    TB_RESOURCE(true, EntityType.TB_RESOURCE);

    private final boolean allEdgesRelated;

    private final EntityType entityType;


    EdgeEventType(boolean allEdgesRelated, EntityType entityType) {
        this.allEdgesRelated = allEdgesRelated;
        this.entityType = entityType;
    }
}
