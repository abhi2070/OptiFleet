package org.thingsboard.server.dao.report;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.Validator;

import java.util.Optional;

import static org.thingsboard.server.dao.service.Validator.validateId;

@Service("ReportDaoService")
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl extends AbstractEntityService implements ReportService {

    public static final String INCORRECT_REPORT_ID = "Incorrect reportId ";
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private ReportInfoDao reportInfoDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DataValidator<Report> reportValidator;

    @Override
    public ReportInfo findReportInfoById(TenantId tenantId, ReportId reportId) {
        validateId(reportId, INCORRECT_REPORT_ID + reportId);
        ReportInfo reportInfo = reportInfoDao.findReportInfoById(tenantId, reportId.getId());
        return reportInfo;
    }

    @Override
    public Report findReportById(TenantId tenantId, ReportId reportId) {
        Validator.validateId(reportId, INCORRECT_REPORT_ID + reportId);
        return reportDao.findById(tenantId, reportId.getId());
    }

    @Override
    public ListenableFuture<Report> findReportByIdAsync(TenantId tenantId, ReportId reportId) {
        validateId(reportId, INCORRECT_REPORT_ID + reportId);
        return reportDao.findByIdAsync(tenantId, reportId.getId());
    }

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findReportById(tenantId, new ReportId(entityId.getId())));
    }

    @Override
    public Report saveReport(Report report, boolean doValidate) {
        return doSaveReport(report, doValidate);
    }

    @Override
    public Report saveReport(Report report) {
        return doSaveReport(report, true);
    }

    @Override
    public Report assignReportToCustomer(TenantId tenantId, ReportId reportId, CustomerId customerId) {
        Report report = findReportById(tenantId, reportId);
        Customer customer = customerDao.findById(tenantId, customerId.getId());
        if (customer == null) throw new DataValidationException("Can't assign report to non-existent customer!");
        if (!customer.getTenantId().getId().equals(report.getTenantId().getId()))
            throw new DataValidationException("Can't assign report to customer from different tenant!");
        if (report.addAssignedCustomer(customer)) {
            try {
                createRelation(tenantId, new EntityRelation(customerId, reportId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.REPORT));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return saveReport(report);
        } else return report;
    }

    @Override
    public Report unassignReportFromCustomer(TenantId tenantId, ReportId reportId, CustomerId customerId) {
        Report report = findReportById(tenantId, reportId);
        Customer customer = customerDao.findById(tenantId, customerId.getId());
        if (customer == null) {
            throw new DataValidationException("Can't unassign report from non-existent customer!");
        }
        if (report.removeAssignedCustomer(customer)) {
            try {
                deleteRelation(tenantId, new EntityRelation(customerId, reportId, EntityRelation.CONTAINS_TYPE, RelationTypeGroup.REPORT));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return saveReport(report);
        } else {
            return report;
        }
    }

    @Override
    public void deleteReport(TenantId tenantId, ReportId reportId) {
        Validator.validateId(reportId, INCORRECT_REPORT_ID + reportId);
        deleteEntityRelations(tenantId, reportId);
        try {
            reportDao.removeById(tenantId, reportId.getId());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public PageData<ReportInfo> findReportsByTenantId(TenantId tenantId, PageLink pageLink) {
        Validator.validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        Validator.validatePageLink(pageLink);
        return reportInfoDao.findReportsByTenantId(tenantId.getId(), pageLink);
    }

    private Report doSaveReport(Report report, boolean doValidate) {
        if (doValidate) {
            reportValidator.validate(report, ReportInfo::getTenantId);
        }
        try {
            Report saved = reportDao.save(report.getTenantId(), report);
            return saved;
        } catch (Exception e) {
            checkConstraintViolation(e, "report_external_id_unq_key", "Report with such external id already exists!");
            throw e;
        }
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.REPORT;
    }

}
