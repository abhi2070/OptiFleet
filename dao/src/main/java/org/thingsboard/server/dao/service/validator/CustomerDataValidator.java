
package org.thingsboard.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.customer.CustomerServiceImpl;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantService;

import java.util.Optional;

@Component
public class CustomerDataValidator extends DataValidator<Customer> {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private TenantService tenantService;

    @Override
    protected void validateCreate(TenantId tenantId, Customer customer) {
        validateNumberOfEntitiesPerTenant(tenantId, EntityType.CUSTOMER);
        customerDao.findCustomersByTenantIdAndTitle(customer.getTenantId().getId(), customer.getTitle()).ifPresent(
                c -> {
                    throw new DataValidationException("Customer with such title already exists!");
                }
        );
    }

    @Override
    protected Customer validateUpdate(TenantId tenantId, Customer customer) {
        Optional<Customer> customerOpt = customerDao.findCustomersByTenantIdAndTitle(customer.getTenantId().getId(), customer.getTitle());
        customerOpt.ifPresent(
                c -> {
                    if (!c.getId().equals(customer.getId())) {
                        throw new DataValidationException("Customer with such title already exists!");
                    }
                }
        );
        return customerOpt.orElse(null);
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Customer customer) {
        validateString("Customer title", customer.getTitle());
        if (customer.getTitle().equals(CustomerServiceImpl.PUBLIC_CUSTOMER_TITLE)) {
            throw new DataValidationException("'Public' title for customer is system reserved!");
        }
        if (!StringUtils.isEmpty(customer.getEmail())) {
            validateEmail(customer.getEmail());
        }
        if (customer.getTenantId() == null) {
            throw new DataValidationException("Customer should be assigned to tenant!");
        } else {
            if (!tenantService.tenantExists(customer.getTenantId())) {
                throw new DataValidationException("Customer is referencing to non-existent tenant!");
            }
        }
    }
}
