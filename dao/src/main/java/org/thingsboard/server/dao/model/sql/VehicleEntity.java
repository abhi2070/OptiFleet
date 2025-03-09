package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.vehicle.Vehicle;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.thingsboard.server.dao.model.ModelConstants.VEHICLE_TABLE_NAME;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = VEHICLE_TABLE_NAME)
public class VehicleEntity extends AbstractVehicleEntity<Vehicle>{

    public VehicleEntity() {
        super();
    }

    public VehicleEntity(Vehicle vehicle) {
        super(vehicle);
    }

    @Override
    public Vehicle toData() {
        return new Vehicle(super.toVehicle());
    }
}
