package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JavaType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Report;
import org.thingsboard.server.common.data.ReportInfo;
import org.thingsboard.server.common.data.ShortCustomerInfo;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.ReportId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;

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
@Table(name = ModelConstants.REPORT_TABLE_NAME)
public class ReportInfoEntity extends BaseSqlEntity<ReportInfo> {

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

    public ReportInfoEntity() {
        super();
    }

    public ReportInfoEntity(ReportInfo reportInfo) {
        if (reportInfo.getId() != null) {
            this.setUuid(reportInfo.getId().getId());
        }
        this.setCreatedTime(reportInfo.getCreatedTime());
        if (reportInfo.getTenantId() != null) {
            this.tenantId = reportInfo.getTenantId().getId();
        }
        this.name = reportInfo.getName();
        this.type = reportInfo.getType();
        this.description = reportInfo.getDescription();
        this.devices = JacksonUtil.toString(reportInfo.getDevices());
        this.telemetryKeys = JacksonUtil.toString(reportInfo.getTelemetryKeys());
        if (reportInfo.getAssignedCustomers() != null) {
            try {
                this.assignedCustomers = JacksonUtil.toString(reportInfo.getAssignedCustomers());
            } catch (IllegalArgumentException e) {
                log.error("Unable to serialize assigned customers to string!", e);
            }
        }
    }

    @Override
    public ReportInfo toData() {
        ReportInfo reportInfo = new ReportInfo(new ReportId(this.getUuid()));
        reportInfo.setCreatedTime(createdTime);
        if (tenantId != null) {
            reportInfo.setTenantId(TenantId.fromUUID(tenantId));
        }
        reportInfo.setName(name);
        reportInfo.setType(type);
        reportInfo.setDescription(description);
        reportInfo.setDevices(JacksonUtil.fromString(devices, List.class));
        reportInfo.setTelemetryKeys(JacksonUtil.fromString(telemetryKeys, List.class));
        if (!StringUtils.isEmpty(assignedCustomers)) {
            try {
                reportInfo.setAssignedCustomers(JacksonUtil.fromString(assignedCustomers, assignedCustomersType));
            } catch (IllegalArgumentException e) {
                log.warn("Unable to parse assigned customers!", e);
            }
        }
        return reportInfo;
    }
}
