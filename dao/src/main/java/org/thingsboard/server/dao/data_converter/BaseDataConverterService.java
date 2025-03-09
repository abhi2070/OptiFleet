package org.thingsboard.server.dao.data_converter;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.asset.AssetProfileInfo;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.data_converter.DataConverter;
import org.thingsboard.server.common.data.data_converter.DataConverterInfo;
import org.thingsboard.server.common.data.data_converter.DataConverterSearchQuery;
import org.thingsboard.server.common.data.data_converter.MetaData;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;

import org.thingsboard.server.dao.entity.AbstractCachedEntityService;
import org.thingsboard.server.dao.entity.EntityCountService;
import org.thingsboard.server.dao.eventsourcing.ActionEntityEvent;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.model.sql.DataConverterEntity;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;
import org.thingsboard.server.dao.service.Validator;

import java.util.*;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.DaoUtil.toUUIDs;
import static org.thingsboard.server.dao.service.Validator.*;

@Service("DataConverterDaoService")
@Slf4j
public class BaseDataConverterService extends AbstractCachedEntityService<DataConverterCacheKey, DataConverter, DataConverterCacheEvictEvent> implements DataConverterService{

    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_DATACONVERTER_ID = "Incorrect data converter Id ";
    public static final String TB_SERVICE_QUEUE = "TbServiceQueue";


    @Autowired
    private DataConverterDao dataConverterDao;

    @Autowired
    private EntityCountService countService;

    @Autowired
    private DataValidator<DataConverter> dataConverterValidator;

    @Override
    public DataConverterInfo findDataConverterInfoById(TenantId tenantId, DataConverterId dataConverterId) {
        validateId(dataConverterId, INCORRECT_DATACONVERTER_ID + dataConverterId);
        return dataConverterDao.findDataConverterInfoById(tenantId, dataConverterId.getId());
    }

    @Override
    public DataConverter findDataConverterById(TenantId tenantId, DataConverterId dataConverterId) {
        validateId(dataConverterId, INCORRECT_DATACONVERTER_ID + dataConverterId);
        return dataConverterDao.findById(tenantId, dataConverterId.getId());
    }

    @Override
    public ListenableFuture<DataConverter> findDataConverterByIdAsync(TenantId tenantId, DataConverterId dataConverterId) {
        validateId(dataConverterId, INCORRECT_DATACONVERTER_ID + dataConverterId);
        return dataConverterDao.findByIdAsync(tenantId, dataConverterId.getId());
    }

    @Override
    public DataConverter findDataConverterByTenantIdAndName(TenantId tenantId, String name) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        return cache.getAndPutInTransaction(new DataConverterCacheKey(tenantId, name),
                () -> (DataConverter) dataConverterDao.findDataConvertersByTenantIdAndName(tenantId.getId(), name)
                        .orElse(null), true);
    }

    @Override
    public DataConverter saveDataConverter(DataConverter dataConverter, boolean doValidate) {
        return  doSaveDataConverter(dataConverter, doValidate);
    }

    @Override
    public DataConverter saveDataConverter(DataConverter dataConverter) {
        return doSaveDataConverter(dataConverter, true);
    }

    private DataConverter doSaveDataConverter(DataConverter dataConverter, boolean doValidate) {
        DataConverter oldDataConverter = null;
        if (doValidate) {
            oldDataConverter = dataConverterValidator.validate(dataConverter, DataConverter::getTenantId);
        } else if (dataConverter.getId() != null) {
            oldDataConverter = findDataConverterById(dataConverter.getTenantId(), dataConverter.getId());
        }
        DataConverterCacheEvictEvent evictEvent = new DataConverterCacheEvictEvent(dataConverter.getTenantId(), dataConverter.getName(), oldDataConverter != null ? oldDataConverter.getName() : null);
        DataConverter savedDataConverter;
        try {
            savedDataConverter = dataConverterDao.save(dataConverter.getTenantId(), dataConverter);
            publishEvictEvent(evictEvent);
            eventPublisher.publishEvent(SaveEntityEvent.builder().tenantId(savedDataConverter.getTenantId())
                    .entityId(savedDataConverter.getId()).created(dataConverter.getId() == null).build());
            if (dataConverter.getId() == null) {
                countService.publishCountEntityEvictEvent(savedDataConverter.getTenantId(), EntityType.DATACONVERTER);
            }
        } catch (Exception t) {
            handleEvictEvent(evictEvent);
            checkConstraintViolation(t,
                    "DataConverter_name_unq_key", "DataConverter with such name already exists!",
                    "DataConverter_external_id_unq_key", "DataConverter with such external id already exists!");
            throw t;
        }
        return savedDataConverter;
    }

    @Override
    public void deleteDataConverter(TenantId tenantId, DataConverterId dataConverterId) {
        validateId(dataConverterId, INCORRECT_DATACONVERTER_ID + dataConverterId);
        if (entityViewService.existsByTenantIdAndEntityId(tenantId, dataConverterId)) {
            throw new DataValidationException("Can't delete data converter that has entity views!");
        }

        DataConverter dataConverter = dataConverterDao.findById(tenantId, dataConverterId.getId());
        alarmService.deleteEntityAlarmRelations(tenantId, dataConverterId);
        deleteDataConverter(tenantId, dataConverter);
    }

    private void deleteDataConverter(TenantId tenantId, DataConverter dataConverter) {
        relationService.deleteEntityRelations(tenantId, dataConverter.getId());
        dataConverterDao.removeById(tenantId, dataConverter.getUuidId());

        publishEvictEvent(new DataConverterCacheEvictEvent(dataConverter.getTenantId(), dataConverter.getName(), null));
        countService.publishCountEntityEvictEvent(tenantId, EntityType.DATACONVERTER);
        eventPublisher.publishEvent(DeleteEntityEvent.builder().tenantId(tenantId).entityId(dataConverter.getId()).build());
    }

    @Override
    public PageData<DataConverter> findDataConverterByTenantId(TenantId tenantId, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return dataConverterDao.findDataConvertersByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public PageData<DataConverterInfo> findDataConverterInfosByTenantId(TenantId tenantId, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return dataConverterDao.findDataConverterInfosByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public PageData<DataConverter> findDataConverterByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return dataConverterDao.findDataConvertersByTenantIdAndType(tenantId.getId(), type, pageLink);
    }

    @Override
    public PageData<DataConverterInfo> findDataConverterInfosByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return dataConverterDao.findDataConverterInfosByTenantIdAndType(tenantId.getId(), type, pageLink);
    }

    @Override
    public ListenableFuture<List<DataConverter>> findDataConverterByTenantIdAndIdsAsync(TenantId tenantId, List<DataConverterId> dataConverterIds) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateIds(dataConverterIds, "Incorrect dataConverterIds " + dataConverterIds);
        return dataConverterDao.findDataConvertersByTenantIdAndIdsAsync(tenantId.getId(), toUUIDs(dataConverterIds));
    }

    @Override
    public void deleteDataConverterByTenantId(TenantId tenantId) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        tenantDataConvertersRemover.removeEntities(tenantId, tenantId);
    }

    @Override
    public ListenableFuture<List<DataConverter>> findDataConverterByQuery(TenantId tenantId, DataConverterSearchQuery query) {
        ListenableFuture<List<EntityRelation>> relations = relationService.findByQuery(tenantId, query.toEntitySearchQuery());
        ListenableFuture<List<DataConverter>> dataConverters = Futures.transformAsync(relations, r -> {
            EntitySearchDirection direction = query.toEntitySearchQuery().getParameters().getDirection();
            List<ListenableFuture<DataConverter>> futures = new ArrayList<>();
            for (EntityRelation relation : r) {
                EntityId entityId = direction == EntitySearchDirection.FROM ? relation.getTo() : relation.getFrom();
                if (entityId.getEntityType() == EntityType.DATACONVERTER) {
                    futures.add(findDataConverterByIdAsync(tenantId, new DataConverterId(entityId.getId())));
                }
            }
            return Futures.successfulAsList(futures);
        }, MoreExecutors.directExecutor());
        dataConverters = Futures.transform(dataConverters, dataConverterList ->
                        dataConverterList == null ?
                                Collections.emptyList() :
                                dataConverterList.stream()
                                        .filter(dataConverter -> query.getDataConverterTypes().contains(dataConverter.getType()))
                                        .collect(Collectors.toList()),
                MoreExecutors.directExecutor()
        );
        return dataConverters;
    }

    @Override
    public ListenableFuture<List<EntitySubtype>> findDataConverterTypesByTenantId(TenantId tenantId) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        return dataConverterDao.findTenantDataConverterTypesAsync(tenantId.getId());
    }

    @Override
    public DataConverter assignDataConverterToEdge(TenantId tenantId, DataConverterId dataConverterId, EdgeId edgeId) {
        DataConverter dataConverter = findDataConverterById(tenantId, dataConverterId);
        Edge edge = edgeService.findEdgeById(tenantId, edgeId);
        if (edge == null) {
            throw new DataValidationException("Can't assign dataConverter to non-existent edge!");
        }
        if (!edge.getTenantId().getId().equals(dataConverter.getTenantId().getId())) {
            throw new DataValidationException("Can't assign dataConverter to edge from different tenant!");
        }
        try {
            createRelation(tenantId, new EntityRelation(edgeId, dataConverterId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.EDGE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eventPublisher.publishEvent(ActionEntityEvent.builder().tenantId(tenantId).edgeId(edgeId).entityId(dataConverterId)
                .actionType(ActionType.ASSIGNED_TO_EDGE).build());
        return dataConverter;
    }

    @Override
    public DataConverter unassignDataConverterFromEdge(TenantId tenantId, DataConverterId dataConverterId, EdgeId edgeId) {
        DataConverter dataConverter = findDataConverterById(tenantId, dataConverterId);
        Edge edge = edgeService.findEdgeById(tenantId, edgeId);
        if (edge == null) {
            throw new DataValidationException("Can't unassign dataConverter from non-existent edge!");
        }

        checkAssignedEntityViewsToEdge(tenantId, dataConverterId, edgeId);

        try {
            deleteRelation(tenantId, new EntityRelation(edgeId, dataConverterId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.EDGE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        eventPublisher.publishEvent(ActionEntityEvent.builder().tenantId(tenantId).edgeId(edgeId).entityId(dataConverterId)
                .actionType(ActionType.UNASSIGNED_FROM_EDGE).build());
        return dataConverter;
    }

    @Override
    public PageData<DataConverter> findDataConverterByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(edgeId, INCORRECT_EDGE_ID + edgeId);
        validatePageLink(pageLink);
        return dataConverterDao.findDataConvertersByTenantIdAndEdgeId(tenantId.getId(), edgeId.getId(), pageLink);
    }

    @Override
    public PageData<DataConverter> findDataConverterByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(edgeId, INCORRECT_EDGE_ID + edgeId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink);
        return dataConverterDao.findDataConvertersByTenantIdAndEdgeIdAndType(tenantId.getId(), edgeId.getId(), type, pageLink);
    }

    private final PaginatedRemover<TenantId, DataConverter> tenantDataConvertersRemover = new PaginatedRemover<>() {

        @Override
        protected PageData<DataConverter> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
            return dataConverterDao.findDataConvertersByTenantId(id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, DataConverter dataConverter) {
            deleteDataConverter(tenantId, dataConverter);
        }
    };

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findDataConverterById(tenantId, new DataConverterId(entityId.getId())));
    }

    @Override
    public long countByTenantId(TenantId tenantId) {
        return dataConverterDao.countByTenantId(tenantId);
    }

    @Override
    @Transactional
    public void deleteEntity(TenantId tenantId, EntityId id) {
        deleteDataConverter(tenantId, (DataConverterId) id);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DATACONVERTER;
    }

    @Override
    public void handleEvictEvent(DataConverterCacheEvictEvent event) {
        List<DataConverterCacheKey> keys = new ArrayList<>(2);
        keys.add(new DataConverterCacheKey(event.getTenantId(), event.getNewName()));
        if (StringUtils.isNotEmpty(event.getOldName()) && !event.getOldName().equals(event.getNewName())) {
            keys.add(new DataConverterCacheKey(event.getTenantId(), event.getOldName()));
        }
        cache.evict(keys);
    }

    @Override
    public PageData<DataConverter> getConverterDebugInfo(String converterId, String converterType) {
        validateString(converterType, "Incorrect converterType " + converterType);
        return dataConverterDao.findConverterDebugInfo(converterId, converterType);
    }

    @Override
    public List<DataConverterEntity> getConverterDebugIn(UUID converterId, String converterType) {
        validateString(converterType, "Incorrect converterType " + converterType);
        List<DataConverterEntity> entities = dataConverterDao.findConverterDebugIn(converterId, converterType);
        return entities != null ? entities : Collections.emptyList();
    }

    @Override
    public PageData<DataConverterInfo> findDataConvertInfos(TenantId tenantId, PageLink pageLink) {
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        Validator.validatePageLink(pageLink);
        return dataConverterDao.findDataConvertInfos(tenantId, pageLink);
    }
}

