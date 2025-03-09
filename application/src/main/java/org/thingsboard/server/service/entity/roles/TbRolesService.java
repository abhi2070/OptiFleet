package org.thingsboard.server.service.entity.roles;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.roles.Roles;

public interface TbRolesService {

    Roles save(Roles roles, User user) throws Exception;

    void delete(Roles roles, User user);

}
