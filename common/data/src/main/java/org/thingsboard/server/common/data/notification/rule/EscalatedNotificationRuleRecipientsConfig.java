
package org.thingsboard.server.common.data.notification.rule;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class EscalatedNotificationRuleRecipientsConfig extends NotificationRuleRecipientsConfig {

    @NotEmpty
    private Map<Integer, List<UUID>> escalationTable;

    @Override
    public Map<Integer, List<UUID>> getTargetsTable() {
        return escalationTable;
    }

}
