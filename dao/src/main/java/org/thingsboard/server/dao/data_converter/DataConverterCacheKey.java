package org.thingsboard.server.dao.data_converter;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.thingsboard.server.common.data.id.TenantId;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class DataConverterCacheKey implements Serializable {

    private static final long serialVersionUID = 4196610233744512673L;

    private final TenantId tenantId;
    private final String name;

    @Override
    public String toString() {
        return tenantId + "_" + name;
    }

}

