
package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.alarm.AlarmComment;
import org.thingsboard.server.common.data.alarm.AlarmCommentType;
import org.thingsboard.server.common.data.id.AlarmCommentId;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.model.BaseEntity;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.ALARM_COMMENT_ALARM_ID;
import static org.thingsboard.server.dao.model.ModelConstants.ALARM_COMMENT_COMMENT;
import static org.thingsboard.server.dao.model.ModelConstants.ALARM_COMMENT_TYPE;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractAlarmCommentEntity<T extends AlarmComment> extends BaseSqlEntity<T> implements BaseEntity<T> {

    @Column(name = ALARM_COMMENT_ALARM_ID, columnDefinition = "uuid")
    private UUID alarmId;

    @Column(name = ModelConstants.ALARM_COMMENT_USER_ID)
    private UUID userId;

    @Column(name = ALARM_COMMENT_TYPE)
    private AlarmCommentType type;

    @Type(type = "json")
    @Column(name = ALARM_COMMENT_COMMENT)
    private JsonNode comment;

    public AbstractAlarmCommentEntity() {
        super();
    }

    public AbstractAlarmCommentEntity(AlarmComment alarmComment) {
        if (alarmComment.getId() != null) {
            this.setUuid(alarmComment.getUuidId());
        }
        this.setCreatedTime(alarmComment.getCreatedTime());
        this.alarmId = alarmComment.getAlarmId().getId();
        if (alarmComment.getUserId() != null) {
            this.userId = alarmComment.getUserId().getId();
        }
        if (alarmComment.getType() != null) {
            this.type = alarmComment.getType();
        }
        this.setComment(alarmComment.getComment());
    }

    public AbstractAlarmCommentEntity(AlarmCommentEntity alarmCommentEntity) {
        this.setId(alarmCommentEntity.getId());
        this.setCreatedTime(alarmCommentEntity.getCreatedTime());
        this.userId = alarmCommentEntity.getUserId();
        this.alarmId = alarmCommentEntity.getAlarmId();
        this.type = alarmCommentEntity.getType();
        this.comment = alarmCommentEntity.getComment();
    }
    protected AlarmComment toAlarmComment() {
        AlarmComment alarmComment = new AlarmComment(new AlarmCommentId(id));
        alarmComment.setCreatedTime(createdTime);
        alarmComment.setAlarmId(new AlarmId(alarmId));
        if (userId != null) {
            alarmComment.setUserId(new UserId(userId));
        }
        alarmComment.setType(type);
        alarmComment.setComment(comment);
        return alarmComment;
    }
}
