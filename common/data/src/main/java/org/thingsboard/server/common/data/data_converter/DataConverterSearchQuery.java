package org.thingsboard.server.common.data.data_converter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntityRelationsQuery;
import org.thingsboard.server.common.data.relation.RelationEntityTypeFilter;
import org.thingsboard.server.common.data.relation.RelationsSearchParameters;

import java.util.Collections;
import java.util.List;

@Data
public class DataConverterSearchQuery {

    @ApiModelProperty(position = 3, value = "Main search parameters.")
    private RelationsSearchParameters parameters;
    @ApiModelProperty(position = 1, value = "Type of the relation between root entity and dataConverter (e.g. 'Contains' or 'Manages').")
    private String relationType;
    @ApiModelProperty(position = 2, value = "Array of dataConverter types to filter the related entities (e.g. 'Building', 'Vehicle').")
    private List<String> dataConverterTypes;

    public EntityRelationsQuery toEntitySearchQuery() {
        EntityRelationsQuery query = new EntityRelationsQuery();
        query.setParameters(parameters);
        query.setFilters(
                Collections.singletonList(new RelationEntityTypeFilter(relationType == null ? EntityRelation.CONTAINS_TYPE : relationType,
                        Collections.singletonList(EntityType.DATACONVERTER))));
        return query;
    }
}


