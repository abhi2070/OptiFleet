
package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.alarm.AlarmComment;
import org.thingsboard.server.common.data.alarm.AlarmCommentInfo;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.thingsboard.server.dao.model.ModelConstants.ALARM_COMMENT_TABLE_NAME;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ALARM_COMMENT_TABLE_NAME)

public class AlarmCommentEntity extends AbstractAlarmCommentEntity<AlarmComment>  {

    public AlarmCommentEntity() {
        super();
    }

    public AlarmCommentEntity(AlarmCommentInfo alarmCommentInfo) {
        super(alarmCommentInfo);
    }

    public AlarmCommentEntity(AlarmComment alarmComment) {
        super(alarmComment);
    }

    @Override
    public AlarmComment toData() {
        return super.toAlarmComment();
    }

}
