
package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.notification.targets.NotificationTarget;
import org.thingsboard.server.common.data.notification.targets.NotificationTargetConfig;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.NOTIFICATION_TARGET_TABLE_NAME)
public class NotificationTargetEntity extends BaseSqlEntity<NotificationTarget> {

    @Column(name = ModelConstants.TENANT_ID_PROPERTY, nullable = false)
    private UUID tenantId;

    @Column(name = ModelConstants.NAME_PROPERTY, nullable = false)
    private String name;

    @Type(type = "json")
    @Column(name = ModelConstants.NOTIFICATION_TARGET_CONFIGURATION_PROPERTY, nullable = false)
    private JsonNode configuration;

    @Column(name = ModelConstants.EXTERNAL_ID_PROPERTY)
    private UUID externalId;

    public NotificationTargetEntity() {}

    public NotificationTargetEntity(NotificationTarget notificationTarget) {
        setId(notificationTarget.getUuidId());
        setCreatedTime(notificationTarget.getCreatedTime());
        setTenantId(getTenantUuid(notificationTarget.getTenantId()));
        setName(notificationTarget.getName());
        setConfiguration(toJson(notificationTarget.getConfiguration()));
        setExternalId(getUuid(notificationTarget.getExternalId()));
    }

    @Override
    public NotificationTarget toData() {
        NotificationTarget notificationTarget = new NotificationTarget();
        notificationTarget.setId(new NotificationTargetId(id));
        notificationTarget.setCreatedTime(createdTime);
        notificationTarget.setTenantId(getTenantId(tenantId));
        notificationTarget.setName(name);
        notificationTarget.setConfiguration(fromJson(configuration, NotificationTargetConfig.class));
        notificationTarget.setExternalId(getEntityId(externalId, NotificationTargetId::new));
        return notificationTarget;
    }

}
