
package org.thingsboard.server.common.data.notification.targets;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.NotificationTargetId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationTarget extends BaseData<NotificationTargetId> implements HasTenantId, HasName, ExportableEntity<NotificationTargetId> {

    private TenantId tenantId;
    @NotBlank
    @NoXss
    @Length(max = 255, message = "cannot be longer than 255 chars")
    private String name;
    @NotNull
    @Valid
    private NotificationTargetConfig configuration;

    private NotificationTargetId externalId;

    public NotificationTarget() {
    }

    public NotificationTarget(NotificationTarget other) {
        super(other);
        this.tenantId = other.tenantId;
        this.name = other.name;
        this.configuration = other.configuration;
        this.externalId = other.externalId;
    }

}
