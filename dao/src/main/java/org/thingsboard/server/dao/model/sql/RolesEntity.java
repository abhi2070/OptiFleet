package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.thingsboard.server.dao.model.ModelConstants.ROLES_TABLE_NAME;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ROLES_TABLE_NAME)
public class RolesEntity extends AbstractRolesEntity<Roles> {

    public RolesEntity() {
        super();
    }

    public RolesEntity(Roles roles) {
        super(roles);
    }


    @Override
    public Roles toData() {
        return new Roles(super.toRoles());
    }

}
