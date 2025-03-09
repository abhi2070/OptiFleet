package org.thingsboard.server.common.data.scheduler;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.thingsboard.server.common.data.BaseDataWithAdditionalInfo;
import org.thingsboard.server.common.data.ExportableEntity;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.SchedulerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.time.LocalDateTime;
import java.util.Optional;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class Scheduler extends BaseDataWithAdditionalInfo<SchedulerId> implements HasTenantId, ExportableEntity<SchedulerId> {

    private static final long serialVersionUID = 2807343040519543363L;
    private TenantId tenantId;
    @NoXss
    @Length(fieldName = "name")
    private String name;
    @NoXss
    @Length(fieldName = "reportNamePattern")
    private String reportNamePattern;
    @NoXss
    @Length(fieldName = "reportType")
    private String reportType;
    @NoXss
    @Length(fieldName = "toAddress")
    private String toAddress;
    @NoXss
    @Length(fieldName = "cc")
    private String cc;
    @NoXss
    @Length(fieldName = "bcc")
    private String bcc;
    @NoXss
    @Length(fieldName = "subject")
    private String subject;
    @NoXss
    @Length(fieldName = "body")
    private String body;
    @NoXss
    @Length(fieldName = "schedule")
    private Boolean schedule;
    @NoXss
    @Length(fieldName = "timeZone")
    private String timeZone;
    @NoXss
    @Length(fieldName = "start")
    private String start;
    @NoXss
    @Length(fieldName = "repeat")
    private Boolean repeat;
    @NoXss
    @Length(fieldName = "repeatSchedule")
    private String repeatSchedule;
    @NoXss
    @Length(fieldName = "endDate")
    private String endDate;
    @NoXss
    @Length(fieldName = "active")
    private boolean active;
    @NoXss
    @Length(fieldName = "reportCategory")
    private String reportCategory;
    @NoXss
    @Length(fieldName = "reportAvailable")
    private String reportAvailable;
    @Getter
    @Setter
    private SchedulerId externalId;

    public Scheduler() { super(); }

    public Scheduler(SchedulerId id) { super(id); }

    public Scheduler(Scheduler scheduler) {
        super(scheduler);
        this.tenantId = scheduler.getTenantId();
        this.name = scheduler.getName();
        this.externalId = scheduler.getExternalId();
        this.toAddress = scheduler.getToAddress();
        this.start = scheduler.getStart();
        this.endDate = scheduler.getEndDate();
        this.cc = scheduler.getcc();
        this.bcc = scheduler.getbcc();
        this.subject = scheduler.getSubject();
        this.timeZone = scheduler.gettimeZone();
        this.body = scheduler.getBody();
        this.repeatSchedule = getRepeatSchedule();
        this.reportNamePattern = getReportNamePattern();
        this.reportType = getReportType();
        this.schedule = getSchedule();
        this.active = getActive();
        this.reportCategory=getReportCategory();
        this.reportAvailable=getReportAvailable();
    }

    public void update(Scheduler scheduler) {
        this.tenantId = scheduler.getTenantId();
        this.name = scheduler.getName();
        this.externalId = scheduler.getExternalId();
        this.toAddress = scheduler.getToAddress();
        this.start = scheduler.getStart();
        this.endDate = scheduler.getEndDate();
        this.cc = scheduler.getcc();
        this.bcc = scheduler.getbcc();
        this.subject = scheduler.getSubject();
        this.timeZone = scheduler.gettimeZone();
        this.body = scheduler.getBody();
        this.repeatSchedule = getRepeatSchedule();
        this.reportNamePattern = getReportNamePattern();
        this.reportType = getReportType();
        this.schedule = getSchedule();
        this.active = getActive();
        this.reportCategory=getReportCategory();
        this.reportAvailable=getReportAvailable();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the scheduler Id. " + "Specify this field to update the scheduler. " + "Referencing non-existing scheduler Id will cause error. " + "Omit this field to create new scheduler.")
    @Override
    public SchedulerId getId() { return super.getId(); }

    @ApiModelProperty(position = 2, value = "Timestamp of the scheduler creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() { return super.getCreatedTime(); }

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public TenantId getTenantId() { return tenantId; }

    public void setTenantId(TenantId tenantId) { this.tenantId = tenantId; }

    @ApiModelProperty(position = 4, required = true, value = "Unique Scheduler Name in scope of Tenant", example = "Air Quality Report")
    @Override
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @ApiModelProperty(position = 5, required = true, value = "start of the scheduler")
    public String getStart() { return start; }

    public void setStart(String start) { this.start = start; }

    @ApiModelProperty(position = 6, required = true, value = "time till the scheduler will work")
    public String getEndDate() { return endDate; }

    public void setEndDate(String endDate) { this.endDate = endDate; }

    @ApiModelProperty(position = 7, required = true, value = "subject of mail")
    public String getSubject() { return subject; }

    public void setSubject(String subject) { this.subject = subject; }

    @ApiModelProperty(position = 8, required = true, value = "timezone ")
    public String gettimeZone() { return timeZone; }

    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    @ApiModelProperty(position = 9, required = false, value = "cc")
    public String getcc() { return cc; }

    public void setCc(String cc) { this.cc = cc; }

    @ApiModelProperty(position = 10, required = false, value = "bcc")
    public String getbcc() { return bcc; }

    public void setBcc(String bcc) { this.bcc = bcc; }

    @ApiModelProperty(position = 12, required = true, value = "toAddress", example = "johndoe@mail.com")
    public String getToAddress() { return toAddress; }

    public void setToAddress(String toAddress) { this.toAddress = toAddress; }

    @ApiModelProperty(position = 13, required = true, value = "repeat")
    public Boolean getRepeat() { return repeat; }

    public void setRepeat(boolean repeat) { this.repeat = repeat; }

    @ApiModelProperty(position = 14, required = true, value = "schedule")
    public void setRepeat(String repeat) { this.repeat = Boolean.valueOf(repeat); }

    @ApiModelProperty(position = 14,required = true, value = "schedule")
    public Boolean getSchedule() { return schedule; }

    public void setSchedule(String schedule) { this.schedule = Boolean.valueOf(schedule); }

    @ApiModelProperty(position = 15, required = true, value = "reportType")
    public String getReportType() { return reportType; }

    public void setReportType(String reportType) { this.reportType = reportType; }

    @ApiModelProperty(position = 16, required = true, value = "email body")
    public String getBody() { return body; }

    public void setBody(String body) { this.body = body; }

    @ApiModelProperty(position = 17, required = true, value = "repeatSchedule")
    public String getRepeatSchedule() { return repeatSchedule; }

    public void setRepeatSchedule(String repeatSchedule) { this.repeatSchedule = repeatSchedule; }

    @ApiModelProperty(position = 18, required = true, value = "reportNamePattern")
    public String getReportNamePattern() { return reportNamePattern; }

    public void setReportNamePattern(String reportNamePattern) { this.reportNamePattern = reportNamePattern; }

    @ApiModelProperty(position = 19, required = true, value = "Active")
    public Boolean getActive() { return active; }

    public void setActive(Boolean active) { this.active = active; }

    @ApiModelProperty(position = 20 ,required=true, value="reportCategory")
    public String getReportCategory(){return reportCategory;}

    public void setReportCategory(String reportCategory){ this.reportCategory=reportCategory;}

    @ApiModelProperty(position = 21 ,required=true, value="reportAvailable")
    public String getReportAvailable(){return reportAvailable;}

    public void setReportAvailable(String reportAvailable){ this.reportAvailable=reportAvailable;}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Scheduler [tenantId=");
        builder.append(tenantId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append(", endDate=");
        builder.append(endDate);
        builder.append(", start=");
        builder.append(start);
        builder.append(", timeZone=");
        builder.append(timeZone);
        builder.append(", subject=");
        builder.append(subject);
        builder.append(", toAddress=");
        builder.append(toAddress);
        builder.append(", reportNamePattern=");
        builder.append(reportNamePattern);
        builder.append(", reportType=");
        builder.append(reportType);
        builder.append(", body=");
        builder.append(body);
        builder.append(", repeatSchedule=");
        builder.append(repeatSchedule);
        builder.append(", active=");
        builder.append(active);
        builder.append(", reportCategory=");
        builder.append(reportCategory);
        builder.append(", reportAvailable=");
        builder.append(reportAvailable);
        builder.append("]");
        return builder.toString();
    }

}