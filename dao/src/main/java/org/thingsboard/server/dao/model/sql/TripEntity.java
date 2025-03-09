package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.Trip;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.thingsboard.server.dao.model.ModelConstants.TRIP_TABLE_NAME;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = TRIP_TABLE_NAME)
public final class TripEntity extends AbstractTripEntity<Trip> {

    public TripEntity() {
        super();
    }

    public TripEntity(Trip trip) {
        super(trip);
    }

    @Override
    public Trip toData() {
        return super.toTrip();
    }

}
