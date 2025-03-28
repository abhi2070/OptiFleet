
package org.thingsboard.server.common.data.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.NotificationRequestId;
import org.thingsboard.server.common.data.id.NotificationRuleId;
import org.thingsboard.server.common.data.id.NotificationTemplateId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.notification.info.NotificationInfo;
import org.thingsboard.server.common.data.notification.template.NotificationTemplate;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest extends BaseData<NotificationRequestId> implements HasTenantId, HasName {

    private TenantId tenantId;
    @NotEmpty
    private List<UUID> targets;

    private NotificationTemplateId templateId;
    @Valid
    private NotificationTemplate template;
    @Valid
    private NotificationInfo info;
    @Valid
    private NotificationRequestConfig additionalConfig;

    private EntityId originatorEntityId;
    private NotificationRuleId ruleId;

    private NotificationRequestStatus status;

    private NotificationRequestStats stats;

    public NotificationRequest(NotificationRequest other) {
        super(other);
        this.tenantId = other.tenantId;
        this.targets = other.targets;
        this.templateId = other.templateId;
        this.template = other.template;
        this.info = other.info;
        this.additionalConfig = other.additionalConfig;
        this.originatorEntityId = other.originatorEntityId;
        this.ruleId = other.ruleId;
        this.status = other.status;
        this.stats = other.stats;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return "To targets " + targets;
    }

    @JsonIgnore
    public UserId getSenderId() {
        return originatorEntityId instanceof UserId ? (UserId) originatorEntityId : null;
    }

    @JsonIgnore
    public boolean isSent() {
        return status == NotificationRequestStatus.SENT;
    }

    @JsonIgnore
    public boolean isScheduled() {
        return status == NotificationRequestStatus.SCHEDULED;
    }

}
