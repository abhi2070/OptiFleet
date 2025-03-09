package org.thingsboard.server.dao.data_converter;

import com.drew.lang.annotations.Nullable;
import lombok.Data;

import java.util.List;

@Data
public class DataConverterTypeFilter {
    @Nullable
    private String relationType;
    @Nullable
    private List<String> DataConverterTypes;
}

