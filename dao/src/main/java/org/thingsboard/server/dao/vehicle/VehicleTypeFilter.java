package org.thingsboard.server.dao.vehicle;

import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

@Data
public class VehicleTypeFilter {

    @Nullable
    private String relationType;
    @Nullable
    private List<String> vehicleTypes;
}
