package org.thingsboard.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Report;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.tenant.TenantService;

@Component
public class ReportDataValidator extends DataValidator<Report> {

    @Autowired
    private TenantService tenantService;

    @Override
    protected void validateCreate(TenantId tenantId, Report report) {
        validateNumberOfEntitiesPerTenant(tenantId, EntityType.REPORT);
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Report report) {
        validateString("Report name", report.getName());
        if (report.getTenantId() == null) {
            throw new DataValidationException("Report should be assigned to tenant!");
        } else {
            if (!tenantService.tenantExists(report.getTenantId())) {
                throw new DataValidationException("Report is referencing to non-existent tenant!");
            }
        }
    }
}
