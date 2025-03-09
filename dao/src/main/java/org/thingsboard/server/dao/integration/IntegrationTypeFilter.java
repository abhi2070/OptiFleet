package org.thingsboard.server.dao.integration;


import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

@Data
public class IntegrationTypeFilter {
    @Nullable
    private String relationType;
    @Nullable
    private List<String> integrationTypes;
}
