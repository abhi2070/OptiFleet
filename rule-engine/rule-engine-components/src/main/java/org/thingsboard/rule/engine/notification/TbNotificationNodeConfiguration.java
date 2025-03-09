
package org.thingsboard.rule.engine.notification;

import lombok.Data;
import org.thingsboard.rule.engine.api.NodeConfiguration;
import org.thingsboard.server.common.data.id.NotificationTemplateId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class TbNotificationNodeConfiguration implements NodeConfiguration<TbNotificationNodeConfiguration> {

    @NotEmpty
    private List<UUID> targets;
    @NotNull
    private NotificationTemplateId templateId;

    @Override
    public TbNotificationNodeConfiguration defaultConfiguration() {
        return new TbNotificationNodeConfiguration();
    }

}
