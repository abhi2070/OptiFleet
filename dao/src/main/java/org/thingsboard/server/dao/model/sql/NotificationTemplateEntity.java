
package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.id.NotificationTemplateId;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.notification.template.NotificationTemplate;
import org.thingsboard.server.common.data.notification.template.NotificationTemplateConfig;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.NOTIFICATION_TEMPLATE_TABLE_NAME)
public class NotificationTemplateEntity extends BaseSqlEntity<NotificationTemplate> {

    @Column(name = ModelConstants.TENANT_ID_PROPERTY, nullable = false)
    private UUID tenantId;

    @Column(name = ModelConstants.NAME_PROPERTY, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.NOTIFICATION_TEMPLATE_NOTIFICATION_TYPE_PROPERTY, nullable = false)
    private NotificationType notificationType;

    @Type(type = "json")
    @Column(name = ModelConstants.NOTIFICATION_TEMPLATE_CONFIGURATION_PROPERTY, nullable = false)
    private JsonNode configuration;

    @Column(name = ModelConstants.EXTERNAL_ID_PROPERTY)
    private UUID externalId;

    public NotificationTemplateEntity() {}

    public NotificationTemplateEntity(NotificationTemplate notificationTemplate) {
        setId(notificationTemplate.getUuidId());
        setCreatedTime(notificationTemplate.getCreatedTime());
        setTenantId(getTenantUuid(notificationTemplate.getTenantId()));
        setName(notificationTemplate.getName());
        setNotificationType(notificationTemplate.getNotificationType());
        setConfiguration(toJson(notificationTemplate.getConfiguration()));
        setExternalId(getUuid(notificationTemplate.getExternalId()));
    }

    @Override
    public NotificationTemplate toData() {
        NotificationTemplate notificationTemplate = new NotificationTemplate();
        notificationTemplate.setId(new NotificationTemplateId(id));
        notificationTemplate.setCreatedTime(createdTime);
        notificationTemplate.setTenantId(getTenantId(tenantId));
        notificationTemplate.setName(name);
        notificationTemplate.setNotificationType(notificationType);
        notificationTemplate.setConfiguration(fromJson(configuration, NotificationTemplateConfig.class));
        notificationTemplate.setExternalId(getEntityId(externalId, NotificationTemplateId::new));
        return notificationTemplate;
    }

}
