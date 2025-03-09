
package org.thingsboard.server.common.data.notification.targets.platform;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Data
public class UserListFilter implements UsersFilter {

    @NotEmpty
    private List<UUID> usersIds;

    @Override
    public UsersFilterType getType() {
        return UsersFilterType.USER_LIST;
    }

}
