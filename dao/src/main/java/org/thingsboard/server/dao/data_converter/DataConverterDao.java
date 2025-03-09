package org.thingsboard.server.dao.data_converter;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.data_converter.DataConverter;
import org.thingsboard.server.common.data.data_converter.DataConverterInfo;
import org.thingsboard.server.common.data.id.DataConverterId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;
import org.thingsboard.server.dao.model.sql.DataConverterEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataConverterDao extends Dao<DataConverter>, TenantEntityDao, ExportableEntityDao<DataConverterId, DataConverter> {

    DataConverterInfo findDataConverterInfoById(TenantId tenantId, UUID id);

    DataConverter save(TenantId tenantId, DataConverter dataConverter);


    Optional<Object> findDataConvertersByTenantIdAndName(UUID id, String name);


    PageData<DataConverter> findDataConvertersByTenantId(UUID id, PageLink pageLink);


    PageData<DataConverterInfo> findDataConverterInfosByTenantId(UUID id, PageLink pageLink);


    PageData<DataConverter> findDataConvertersByTenantIdAndType(UUID id, String type, PageLink pageLink);


    PageData<DataConverterInfo> findDataConverterInfosByTenantIdAndType(UUID id, String type, PageLink pageLink);

    ListenableFuture<List<DataConverter>> findDataConvertersByTenantIdAndIdsAsync(UUID id, List<UUID> uuiDs);

    ListenableFuture<List<EntitySubtype>> findTenantDataConverterTypesAsync(UUID id);


    PageData<DataConverter> findDataConvertersByTenantIdAndEdgeIdAndType(UUID id, UUID id1, String type, PageLink pageLink);


    PageData<DataConverter> findDataConvertersByTenantIdAndEdgeId(UUID id, UUID id1, PageLink pageLink);

    PageData<DataConverter> findConverterDebugInfo(String converterId, String converterType);

    List<DataConverterEntity> findConverterDebugIn(UUID converterId, String converterType);


    PageData<DataConverterInfo> findDataConvertInfos(TenantId tenantId, PageLink pageLink);
}
