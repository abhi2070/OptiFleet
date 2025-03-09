package org.thingsboard.server.dao.sql.data_converter;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.asset.AssetProfileInfo;
import org.thingsboard.server.common.data.data_converter.DataConverter;
import org.thingsboard.server.common.data.data_converter.DataConverterInfo;
import org.thingsboard.server.common.data.id.DataConverterId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.data_converter.DataConverterDao;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.model.sql.*;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@SqlDao
@Slf4j
public class JpaDataConverterDao extends JpaAbstractDao<DataConverterEntity, DataConverter> implements DataConverterDao {

    @Autowired
    private DataConverterRepository dataConverterRepository;

    public static final String TB_SERVICE_QUEUE = "TbServiceQueue";


    @Override
    public DataConverterInfo findDataConverterInfoById(TenantId tenantId, UUID dataConverterId) {
        DataConverterInfoEntity dc = new DataConverterInfoEntity();
        return DaoUtil.getData(dc);
    }

    @Override
    public Optional<Object> findDataConvertersByTenantIdAndName(UUID id, String name) {
        return Optional.empty();
    }

    @Override
    public PageData<DataConverter> findDataConvertersByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(dataConverterRepository
                .findByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<DataConverterInfo> findDataConverterInfosByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                dataConverterRepository.findDataConverterInfosByTenantId(
                        tenantId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<DataConverter> findDataConvertersByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(dataConverterRepository
                .findByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<DataConverterInfo> findDataConverterInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(
                dataConverterRepository.findDataConverterInfosByTenantIdAndType(
                        tenantId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public ListenableFuture<List<DataConverter>> findDataConvertersByTenantIdAndIdsAsync(UUID tenantId, List<UUID> dataConverterIds) {
        return service.submit(() ->
                DaoUtil.convertDataList(dataConverterRepository.findByTenantIdAndIdIn(tenantId, dataConverterIds)));
    }

    @Override
    public ListenableFuture<List<EntitySubtype>> findTenantDataConverterTypesAsync(UUID id) {
        return null;
    }

    @Override
    public PageData<DataConverter> findDataConvertersByTenantIdAndEdgeIdAndType(UUID tenantId,UUID edgeId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(dataConverterRepository
                .findByTenantIdAndEdgeIdAndType(
                        tenantId,
                        edgeId,
                        type,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<DataConverter> findDataConvertersByTenantIdAndEdgeId(UUID tenantId,UUID edgeId, PageLink pageLink) {
        return DaoUtil.toPageData(dataConverterRepository
                .findByTenantIdAndEdgeId(
                        tenantId,
                        edgeId,
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public DataConverter findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
//        return DaoUtil.getData(dataConverterRepository.findByTenantIdAndExternalId(tenantId, externalId));
        return null;
    }

    @Override
    public PageData<DataConverter> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findDataConvertersByTenantId(tenantId, pageLink);
    }

    @Override
    public DataConverterId getExternalIdByInternal(DataConverterId internalId) {
        return null;
    }

    @Override
    public Long countByTenantId(TenantId tenantId) {
        return dataConverterRepository.countByTenantIdAndTypeIsNot(tenantId.getId(), TB_SERVICE_QUEUE);
    }

    @Override
    protected Class<DataConverterEntity> getEntityClass() {
        return DataConverterEntity.class;
    }

    @Override
    protected JpaRepository<DataConverterEntity, UUID> getRepository() {
        return dataConverterRepository;
    }

    @Override
    public PageData<DataConverter> findConverterDebugInfo(String converterId, String converterType) {
        List<DataConverterEntity> entities = dataConverterRepository.findConverterDebugInfo(converterId, converterType);
        return DaoUtil.toPageData((Page<? extends ToData<DataConverter>>) entities);
    }

    @Override
    public List<DataConverterEntity> findConverterDebugIn(UUID converterId, String converterType) {
        List<DataConverterEntity> entities = dataConverterRepository.findConverterDebugIn(converterId, converterType);
        return entities;
    }

    @Override
    public PageData<DataConverterInfo> findDataConvertInfos(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(
                dataConverterRepository.findDataConvertInfos(
                        tenantId.getId(),
                        pageLink.getTextSearch(),
                        DaoUtil.toPageable(pageLink)));
    }
}

