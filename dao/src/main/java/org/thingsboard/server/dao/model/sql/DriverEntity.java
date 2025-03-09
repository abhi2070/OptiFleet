package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.driver.Driver;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.thingsboard.server.dao.model.ModelConstants.DRIVER_TABLE_NAME;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = DRIVER_TABLE_NAME)
public final class DriverEntity extends AbstractDriverEntity<Driver> {

    public DriverEntity() {
        super();
    }

    public DriverEntity(Driver driver) {
        super(driver);
    }

    @Override
    public Driver toData() {
        return super.toDriver();
    }
}
