
package org.thingsboard.server.common.data.notification.rule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.NotificationRuleId;
import org.thingsboard.server.common.data.id.NotificationTemplateId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerConfig;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationRule extends BaseData<NotificationRuleId> implements HasTenantId, HasName, ExportableEntity<NotificationRuleId>, Serializable {

    private TenantId tenantId;
    @NotBlank
    @NoXss
    @Length(max = 255, message = "cannot be longer than 255 chars")
    private String name;
    private boolean enabled;
    @NotNull
    private NotificationTemplateId templateId;

    @NotNull
    private NotificationRuleTriggerType triggerType;
    @NotNull
    @Valid
    private NotificationRuleTriggerConfig triggerConfig;
    @NotNull
    @Valid
    private NotificationRuleRecipientsConfig recipientsConfig;

    private NotificationRuleConfig additionalConfig;

    private NotificationRuleId externalId;

    public NotificationRule(NotificationRule other) {
        super(other);
        this.tenantId = other.tenantId;
        this.name = other.name;
        this.enabled = other.enabled;
        this.templateId = other.templateId;
        this.triggerType = other.triggerType;
        this.triggerConfig = other.triggerConfig;
        this.recipientsConfig = other.recipientsConfig;
        this.additionalConfig = other.additionalConfig;
        this.externalId = other.externalId;
    }

    @JsonIgnore
    @AssertTrue(message = "trigger type not matching")
    public boolean isValid() {
        return triggerType == triggerConfig.getTriggerType() &&
                triggerType == recipientsConfig.getTriggerType();
    }

    @JsonIgnore
    public String getDeduplicationKey() {
        String targets = recipientsConfig.getTargetsTable().values().stream()
                .flatMap(List::stream).sorted().map(Object::toString)
                .collect(Collectors.joining(","));
        return String.join(":", targets, triggerConfig.getDeduplicationKey());
    }

}
