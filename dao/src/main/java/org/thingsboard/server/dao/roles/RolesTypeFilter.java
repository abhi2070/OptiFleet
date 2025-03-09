package org.thingsboard.server.dao.roles;

import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by utsav on 07.04.24.
 */
@Data
public class RolesTypeFilter {
    @Nullable
    private String relationType;
    @Nullable
    private List<String> rolesTypes;
}
