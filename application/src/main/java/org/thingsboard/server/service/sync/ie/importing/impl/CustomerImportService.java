
package org.thingsboard.server.service.sync.ie.importing.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.sync.ie.EntityExportData;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.customer.CustomerService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.sync.vc.data.EntitiesImportCtx;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class CustomerImportService extends BaseEntityImportService<CustomerId, Customer, EntityExportData<Customer>> {

    private final CustomerService customerService;
    private final CustomerDao customerDao;

    @Override
    protected void setOwner(TenantId tenantId, Customer customer, IdProvider idProvider) {
        customer.setTenantId(tenantId);
    }

    @Override
    protected Customer prepare(EntitiesImportCtx ctx, Customer customer, Customer old, EntityExportData<Customer> exportData, IdProvider idProvider) {
        if (customer.isPublic()) {
            Customer publicCustomer = customerService.findOrCreatePublicCustomer(ctx.getTenantId());
            publicCustomer.setExternalId(customer.getExternalId());
            return publicCustomer;
        } else {
            return customer;
        }
    }

    @Override
    protected Customer saveOrUpdate(EntitiesImportCtx ctx, Customer customer, EntityExportData<Customer> exportData, IdProvider idProvider) {
        if (!customer.isPublic()) {
            return customerService.saveCustomer(customer);
        } else {
            return customerDao.save(ctx.getTenantId(), customer);
        }
    }

    @Override
    protected Customer deepCopy(Customer customer) {
        return new Customer(customer);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.CUSTOMER;
    }

}
