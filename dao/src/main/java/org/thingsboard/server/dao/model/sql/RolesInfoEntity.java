package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.roles.RolesInfo;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

import static org.thingsboard.server.dao.model.ModelConstants.ASSET_TABLE_NAME;
import static org.thingsboard.server.dao.model.ModelConstants.ROLES_TABLE_NAME;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ROLES_TABLE_NAME)
public class RolesInfoEntity extends AbstractRolesEntity<RolesInfo> {

    public static final Map<String,String> rolesInfoColumnMap = new HashMap<>();

    public RolesInfoEntity() {
        super();
    }

    public RolesInfoEntity(RolesEntity rolesEntity) {
        super(rolesEntity);
    }

    @Override
    public RolesInfo toData() {
        return new RolesInfo(super.toRoles());
    }
}
