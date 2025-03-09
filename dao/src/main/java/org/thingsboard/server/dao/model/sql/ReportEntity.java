package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JavaType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Report;
import org.thingsboard.server.common.data.ShortCustomerInfo;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.ReportId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.REPORT_TABLE_NAME)
public final class ReportEntity extends BaseSqlEntity<Report> {

    private static final JavaType assignedCustomersType = JacksonUtil.constructCollectionType(HashSet.class, ShortCustomerInfo.class);

    @Column(name = ModelConstants.REPORT_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = ModelConstants.REPORT_NAME)
    private String name;

    @Column(name = ModelConstants.REPORT_TYPE_PROPERTY)
    private String type;

    @Column(name = ModelConstants.REPORT_DESCRIPTION_PROPERTY)
    private String description;

    @Column(name = ModelConstants.REPORT_DEVICES_PROPERTY)
    private String devices;

    @Column(name = ModelConstants.REPORT_TELEMETRY_KEYS_PROPERTY)
    private String telemetryKeys;

    @Column(name = ModelConstants.REPORT_ASSIGNED_CUSTOMERS_PROPERTY)
    private String assignedCustomers;

    @Column(name = ModelConstants.EXTERNAL_ID_PROPERTY)
    private UUID externalId;

    public ReportEntity() {
        super();
    }

    public ReportEntity(Report report) {
        if (report.getId() != null) {
            this.setUuid(report.getId().getId());
        }
        this.setCreatedTime(report.getCreatedTime());
        if (report.getTenantId() != null) {
            this.tenantId = report.getTenantId().getId();
        }
        this.name = report.getName();
        this.type = report.getType();
        this.description = report.getDescription();
        this.devices = JacksonUtil.toString(report.getDevices());
        this.telemetryKeys = JacksonUtil.toString(report.getTelemetryKeys());
        if (report.getAssignedCustomers() != null) {
            try {
                this.assignedCustomers = JacksonUtil.toString(report.getAssignedCustomers());
            } catch (IllegalArgumentException e) {
                log.error("Unable to serialize assigned customers to string!", e);
            }
        }
        if (report.getExternalId() != null) {
            this.externalId = report.getExternalId().getId();
        }
    }

    @Override
    public Report toData() {
        Report report = new Report(new ReportId(this.getUuid()));
        report.setCreatedTime(this.getCreatedTime());
        if (tenantId != null) {
            report.setTenantId(TenantId.fromUUID(tenantId));
        }
        report.setName(name);
        report.setType(type);
        report.setDescription(description);
        report.setDevices(JacksonUtil.fromString(devices, List.class));
        report.setTelemetryKeys(JacksonUtil.fromString(telemetryKeys, List.class));
        if (!StringUtils.isEmpty(assignedCustomers)) {
            try {
                report.setAssignedCustomers(JacksonUtil.fromString(assignedCustomers, assignedCustomersType));
            } catch (IllegalArgumentException e) {
                log.warn("Unable to parse assigned customers!", e);
            }
        }
        if (externalId != null) {
            report.setExternalId(new ReportId(externalId));
        }
        return report;
    }

}
