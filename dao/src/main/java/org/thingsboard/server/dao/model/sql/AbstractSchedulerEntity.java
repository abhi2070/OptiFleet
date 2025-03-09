package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.common.data.id.SchedulerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractSchedulerEntity<T extends Scheduler> extends BaseSqlEntity<T> {

    @Column(name = SCHEDULER_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = SCHEDULER_NAME_PROPERTY)
    private String name;

    @Column(name = SCHEDULER_REPORT_NAME_PATTERN_PROPERTY)
    private String reportNamePattern;

    @Column(name = SCHEDULER_REPORT_TYPE_PROPERTY)
    private String reportType;

    @Column(name = SCHEDULER_TO_ADDRESS_PROPERTY)
    private String toAddress;

    @Column(name = SCHEDULER_CC_PROPERTY)
    private String cc;

    @Column(name = SCHEDULER_BCC_PROPERTY)
    private String bcc;

    @Column(name = SCHEDULER_SUBJECT_PROPERTY)
    private String subject;

    @Column(name = SCHEDULER_SCHEDULE_PROPERTY)
    private Boolean schedule;

    @Column(name = SCHEDULER_TIMEZONE_PROPERTY)
    private String timeZone;

    @Column(name = SCHEDULER_START_PROPERTY)
    private String start;

    @Column(name = SCHEDULER_REPEAT_PROPERTY)
    private Boolean repeat;

    @Column(name = SCHEDULER_BODY_PROPERTY)
    private String body;

    @Column(name = SCHEDULER_END_DATE_PROPERTY)
    private String endDate;

    @Type(type = "json")
    @Column(name = SCHEDULER_ADDITIONAL_INFO_PROPERTY)
    private JsonNode additionalInfo;

    @Column(name = EXTERNAL_ID_PROPERTY)
    private UUID externalId;

    @Column(name=SCHEDULER_REPEAT_SCHEDULER)
    private String repeatSchedule;

    @Column(name = SCHEDULER_REPEAT_ACTIVE)
    private Boolean active;

    @Column(name = SCHEDULER_REPORT_CATEGORY)
    private String reportCategory;

    @Column(name = SCHEDULER_REPORT_AVAILABLE)
    private String reportAvailable;

    public AbstractSchedulerEntity() {
        super();
    }

    public AbstractSchedulerEntity(Scheduler scheduler) {
        if (scheduler.getId() != null) {
            this.setUuid(scheduler.getId().getId());
        }
        this.setCreatedTime(scheduler.getCreatedTime());
        if (scheduler.getTenantId() != null) {
            this.tenantId = scheduler.getTenantId().getId();
        }
        this.name = scheduler.getName();
        this.reportType = scheduler.getReportType();
        this.toAddress = scheduler.getToAddress();
        this.cc = scheduler.getcc();
        this.bcc = scheduler.getbcc();
        this.subject = scheduler.getSubject();
        this.schedule = scheduler.getSchedule();
        this.timeZone = scheduler.gettimeZone();
        this.start = scheduler.getStart();
        this.repeat = scheduler.getRepeat();
        this.endDate = scheduler.getEndDate();
        this.additionalInfo = scheduler.getAdditionalInfo();
        this.body=scheduler.getBody();
        this.active=scheduler.getActive();
        this.repeatSchedule=scheduler.getRepeatSchedule();
        this.reportAvailable=scheduler.getReportAvailable();
        this.reportCategory=scheduler.getReportCategory();
        if (scheduler.getExternalId() != null) {
            this.externalId = scheduler.getExternalId().getId();
        }
        this.reportNamePattern=scheduler.getReportNamePattern();
    }

    public AbstractSchedulerEntity(SchedulerEntity schedulerEntity) {
        this.setId(schedulerEntity.getId());
        this.setCreatedTime(schedulerEntity.getCreatedTime());
        this.tenantId = schedulerEntity.getTenantId();
        this.name = schedulerEntity.getName();
        this.reportType = schedulerEntity.getReportType();
        this.toAddress = schedulerEntity.getToAddress();
        this.cc = schedulerEntity.getCc();
        this.bcc = schedulerEntity.getBcc();
        this.subject = schedulerEntity.getSubject();
        this.schedule = schedulerEntity.getSchedule();
        this.timeZone = schedulerEntity.getTimeZone();
        this.start = schedulerEntity.getStart();
        this.repeat = schedulerEntity.getRepeat();
        this.endDate = schedulerEntity.getEndDate();
        this.additionalInfo = schedulerEntity.getAdditionalInfo();
        this.externalId = schedulerEntity.getExternalId();
        this.reportNamePattern=schedulerEntity.getReportNamePattern();
        this.active=schedulerEntity.getActive();
        this.reportCategory=schedulerEntity.getReportCategory();
        this.reportAvailable=schedulerEntity.getReportAvailable();

    }

    protected Scheduler toScheduler() {
        Scheduler scheduler = new Scheduler(new SchedulerId(id));
        scheduler.setCreatedTime(createdTime);
        if (tenantId != null) {
            scheduler.setTenantId(TenantId.fromUUID(tenantId));
        }
        scheduler.setName(name);
        scheduler.setReportType(reportType);
        scheduler.setToAddress(toAddress);
        scheduler.setCc(cc);
        scheduler.setBcc(bcc);
        scheduler.setSubject(subject);
        scheduler.setTimeZone(timeZone);
        scheduler.setStart(start);
        scheduler.setEndDate(endDate);
        scheduler.setBody(body);
        scheduler.setRepeat(String.valueOf(repeat));
        scheduler.setSchedule(String.valueOf(schedule));
        scheduler.setAdditionalInfo(additionalInfo);
        scheduler.setRepeatSchedule(repeatSchedule);
        scheduler.setReportNamePattern(reportNamePattern);
        scheduler.setActive(active);
        scheduler.setReportCategory(reportCategory);
        scheduler.setReportAvailable(reportAvailable);
        if (externalId != null) {
            scheduler.setExternalId(new SchedulerId(externalId));
        }
        return scheduler;
    }
}
