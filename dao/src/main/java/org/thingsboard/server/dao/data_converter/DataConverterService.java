package org.thingsboard.server.dao.data_converter;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.data_converter.DataConverter;
import org.thingsboard.server.common.data.data_converter.DataConverterInfo;
import org.thingsboard.server.common.data.data_converter.DataConverterSearchQuery;
import org.thingsboard.server.common.data.data_converter.MetaData;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.EntityDaoService;
import org.thingsboard.server.dao.model.sql.DataConverterEntity;

import java.util.List;
import java.util.UUID;


public interface DataConverterService extends EntityDaoService {
    DataConverterInfo findDataConverterInfoById(TenantId tenantId, DataConverterId dataConverterId);

    DataConverter findDataConverterById(TenantId tenantId, DataConverterId dataConverterId);

    ListenableFuture<DataConverter> findDataConverterByIdAsync(TenantId tenantId, DataConverterId dataConverterId);

    DataConverter findDataConverterByTenantIdAndName(TenantId tenantId, String name);

    DataConverter saveDataConverter(DataConverter dataConverter, boolean doValidate);

    DataConverter saveDataConverter(DataConverter dataConverter);

    void deleteDataConverter(TenantId tenantId, DataConverterId dataConverterId);

    PageData<DataConverter> findDataConverterByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<DataConverterInfo> findDataConverterInfosByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<DataConverter> findDataConverterByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    PageData<DataConverterInfo> findDataConverterInfosByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    ListenableFuture<List<DataConverter>> findDataConverterByTenantIdAndIdsAsync(TenantId tenantId, List<DataConverterId> dataConverterIds);

    void deleteDataConverterByTenantId(TenantId tenantId);

    ListenableFuture<List<DataConverter>> findDataConverterByQuery(TenantId tenantId, DataConverterSearchQuery query);

//    @Deprecated(since = "3.6.2", forRemoval = true)
    ListenableFuture<List<EntitySubtype>> findDataConverterTypesByTenantId(TenantId tenantId);

    DataConverter assignDataConverterToEdge(TenantId tenantId, DataConverterId dataConverterId, EdgeId edgeId);

    DataConverter unassignDataConverterFromEdge(TenantId tenantId, DataConverterId dataConverterId, EdgeId edgeId);

    PageData<DataConverter> findDataConverterByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink);

    PageData<DataConverter> findDataConverterByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink);

    PageData<DataConverter> getConverterDebugInfo(String converterId, String converterType);

    List<DataConverterEntity> getConverterDebugIn(UUID converterId, String converterType);

    PageData<DataConverterInfo> findDataConvertInfos(TenantId tenantId, PageLink pageLink);
}

