package org.thingsboard.server.common.data.roles;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.RolesId;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class RolesInfo extends Roles {
    private static final long serialVersionUID = -4094528227011066194L;


    public RolesInfo() {
        super();
    }

    public RolesInfo(RolesId rolesId) {
        super(rolesId);
    }

    public RolesInfo(Roles roles) {
        super(roles);
    }


}
