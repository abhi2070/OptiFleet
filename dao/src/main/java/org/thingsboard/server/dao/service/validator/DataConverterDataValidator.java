package org.thingsboard.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.data_converter.DataConverter;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.asset.AssetDao;
import org.thingsboard.server.dao.asset.BaseAssetService;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.data_converter.DataConverterDao;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantService;

import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;

@Component
public class DataConverterDataValidator extends DataValidator<DataConverter> {

    @Autowired
    private DataConverterDao dataConverterDao;

    @Autowired
    @Lazy
    private TenantService tenantService;

    @Autowired
    private CustomerDao customerDao;

    @Override
    protected void validateCreate(TenantId tenantId, DataConverter dataConverter) {
        if (!BaseAssetService.TB_SERVICE_QUEUE.equals(dataConverter.getType())) {
            validateNumberOfEntitiesPerTenant(tenantId, EntityType.ASSET);
        }
    }

    @Override
    protected DataConverter  validateUpdate(TenantId tenantId, DataConverter dataConverter ) {
        DataConverter  old = dataConverterDao.findById(dataConverter .getTenantId(), dataConverter .getId().getId());
        if (old == null) {
            throw new DataValidationException("Can't update non existing dataConverter!");
        }
        return old;
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, DataConverter dataConverter) {
        validateString("DataConverter name", dataConverter.getName());
        if (dataConverter.getTenantId() == null) {
            throw new DataValidationException("DataConverter should be assigned to tenant!");
        } else {
            if (!tenantService.tenantExists(dataConverter.getTenantId())) {
                throw new DataValidationException("DataConverter is referencing to non-existent tenant!");
            }
        }
//        if (dataConverter.getCustomerId() == null) {
//            dataConverter.setCustomerId(new CustomerId(NULL_UUID));
//        } else if (!dataConverter.getCustomerId().getId().equals(NULL_UUID)) {
//            Customer customer = customerDao.findById(tenantId, dataConverter.getCustomerId().getId());
//            if (customer == null) {
//                throw new DataValidationException("Can't assign dataConverter to non-existent customer!");
//            }
//            if (!customer.getTenantId().equals(dataConverter.getTenantId())) {
//                throw new DataValidationException("Can't assign dataConverter to customer from different tenant!");
//            }
//        }
    }




}

