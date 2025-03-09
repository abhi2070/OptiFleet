
package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.id.NotificationId;
import org.thingsboard.server.common.data.id.NotificationRequestId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.Notification;
import org.thingsboard.server.common.data.notification.NotificationStatus;
import org.thingsboard.server.common.data.notification.NotificationType;
import org.thingsboard.server.common.data.notification.info.NotificationInfo;
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
@Table(name = ModelConstants.NOTIFICATION_TABLE_NAME)
public class NotificationEntity extends BaseSqlEntity<Notification> {

    @Column(name = ModelConstants.NOTIFICATION_REQUEST_ID_PROPERTY, nullable = false)
    private UUID requestId;

    @Column(name = ModelConstants.NOTIFICATION_RECIPIENT_ID_PROPERTY, nullable = false)
    private UUID recipientId;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.NOTIFICATION_TYPE_PROPERTY, nullable = false)
    private NotificationType type;

    @Column(name = ModelConstants.NOTIFICATION_SUBJECT_PROPERTY)
    private String subject;

    @Column(name = ModelConstants.NOTIFICATION_TEXT_PROPERTY, nullable = false)
    private String text;

    @Type(type = "json")
    @Column(name = ModelConstants.NOTIFICATION_ADDITIONAL_CONFIG_PROPERTY)
    private JsonNode additionalConfig;

    @Type(type = "json")
    @Formula("(SELECT r.info FROM notification_request r WHERE r.id = request_id)")
    private JsonNode info;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.NOTIFICATION_STATUS_PROPERTY)
    private NotificationStatus status;

    public NotificationEntity() {}

    public NotificationEntity(Notification notification) {
        setId(notification.getUuidId());
        setCreatedTime(notification.getCreatedTime());
        setRequestId(getUuid(notification.getRequestId()));
        setRecipientId(getUuid(notification.getRecipientId()));
        setType(notification.getType());
        setSubject(notification.getSubject());
        setText(notification.getText());
        setAdditionalConfig(notification.getAdditionalConfig());
        setInfo(toJson(notification.getInfo()));
        setStatus(notification.getStatus());
    }

    @Override
    public Notification toData() {
        Notification notification = new Notification();
        notification.setId(new NotificationId(id));
        notification.setCreatedTime(createdTime);
        notification.setRequestId(getEntityId(requestId, NotificationRequestId::new));
        notification.setRecipientId(getEntityId(recipientId, UserId::new));
        notification.setType(type);
        notification.setSubject(subject);
        notification.setText(text);
        notification.setAdditionalConfig(additionalConfig);
        notification.setInfo(fromJson(info, NotificationInfo.class));
        notification.setStatus(status);
        return notification;
    }

}
