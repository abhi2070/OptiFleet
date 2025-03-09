
package org.thingsboard.server.common.data.notification.targets.platform;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CustomerUsersFilter implements UsersFilter {

    @NotNull
    private UUID customerId;

    @Override
    public UsersFilterType getType() {
        return UsersFilterType.CUSTOMER_USERS;
    }

}
