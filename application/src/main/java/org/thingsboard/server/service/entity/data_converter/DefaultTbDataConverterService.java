package org.thingsboard.server.service.entity.data_converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.data_converter.DataConverter;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DataConverterId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.data_converter.DataConverterService;
import org.thingsboard.server.dao.model.sql.DataConverterEntity;
import org.thingsboard.server.dao.sql.data_converter.DataConverterRepository;
import org.thingsboard.server.service.entity.AbstractTbEntityService;

import javax.transaction.Transactional;

import static org.thingsboard.server.dao.asset.BaseAssetService.TB_SERVICE_QUEUE;
import static org.thingsboard.server.dao.roles.BaseRolesService.DUPLICATE_MESSAGE;

@Service
@Slf4j
public class DefaultTbDataConverterService extends AbstractTbEntityService implements TbDataConverterService{

    private final DataConverterService dataConverterService;
    private final DataConverterRepository dataConverterRepository;

    public DefaultTbDataConverterService(DataConverterService dataConverterService, DataConverterRepository dataConverterRepository) {
        this.dataConverterService = dataConverterService;
        this.dataConverterRepository = dataConverterRepository;
    }

    @Override
    @Transactional
    public DataConverter save(DataConverter dataConverter, User user) throws Exception {
        ActionType actionType = dataConverter.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = dataConverter.getTenantId();

        DataConverterEntity existingDataConverterEntity = dataConverterRepository.findByNameAndType(dataConverter.getName(), dataConverter.getType());
        if (existingDataConverterEntity != null) {
            if (dataConverter.getId() == null || !existingDataConverterEntity.getId().equals(dataConverter.getId())) {
                if (!existingDataConverterEntity.getName().equals(dataConverter.getName()) ||
                        !existingDataConverterEntity.getType().equals(dataConverter.getType())) {
                    throw new ThingsboardException("Data transformation with " + DUPLICATE_MESSAGE, ThingsboardErrorCode.DUPLICATE_ENTRY);
                }
            }
        }

        try {
            if (TB_SERVICE_QUEUE.equals(dataConverter.getType())) {
                throw new ThingsboardException("Unable to save dataConverter with type " + TB_SERVICE_QUEUE, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            DataConverter savedDataConverter = checkNotNull(dataConverterService.saveDataConverter(dataConverter));
            autoCommit(user, savedDataConverter.getId());
            return savedDataConverter;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DATACONVERTER), dataConverter, actionType, user, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(DataConverter dataConverter, User user) {
        ActionType actionType = ActionType.DELETED;
        TenantId tenantId = dataConverter.getTenantId();
        DataConverterId dataConverterId = dataConverter.getId();
        try {
            removeAlarmsByEntityId(tenantId, dataConverterId);
            dataConverterService.deleteDataConverter(tenantId, dataConverterId);
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DATACONVERTER), actionType, user, e,
                    dataConverterId.toString());
            throw e;
        }
    }

    @Override
    public DataConverter assignDataConverterToCustomer(TenantId tenantId, DataConverterId dataConverterId, Customer customer, User user) throws ThingsboardException {
        return null;
    }

    @Override
    public DataConverter unassignDataConverterToCustomer(TenantId tenantId, DataConverterId dataConverterId, Customer customer, User user) throws ThingsboardException {
        return null;
    }



    @Override
    public DataConverter assignDataConverterToPublicCustomer(TenantId tenantId, DataConverterId dataConverterId, User user) throws ThingsboardException {
        return null;
    }

    @Override
    public DataConverter assignDataConverterToEdge(TenantId tenantId, DataConverterId dataConverterId, Edge edge, User user) throws ThingsboardException {
        return null;
    }

    @Override
    public DataConverter unassignDataConverterFromEdge(TenantId tenantId, DataConverter dataConverter, Edge edge, User user) throws ThingsboardException {
        return null;
    }
}
