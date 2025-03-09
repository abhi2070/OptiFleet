package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.ReportId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportInfo extends BaseData<ReportId> implements HasName, HasTenantId {

    private static final long serialVersionUID = 2807343040519543363L;

    private TenantId tenantId;
    @NoXss
    @Length(fieldName = "name")
    private String name;
    @NoXss
    @Length(fieldName = "type")
    private String type;
    @NoXss
    @Length(fieldName = "description")
    private String description;
    @NoXss
    @Length(fieldName = "devices")
    private List<String> devices;
    @NoXss
    @Length(fieldName = "telemetryKeys")
    private List<String> telemetryKeys;
    @Valid
    private Set<ShortCustomerInfo> assignedCustomers;

    public ReportInfo() {
        super();
    }

    public ReportInfo(ReportId id) {
        super(id);
    }

    public ReportInfo(ReportInfo reportInfo) {
        super(reportInfo);
        this.tenantId = reportInfo.getTenantId();
        this.name = reportInfo.getName();
        this.type = reportInfo.getType();
        this.description = reportInfo.getDescription();
        this.devices = reportInfo.getDevices();
        this.telemetryKeys = reportInfo.getTelemetryKeys();
        this.assignedCustomers = reportInfo.getAssignedCustomers();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the Report Id. " + "Specify this field to update the Report. " + "Referencing non-existing Report Id will cause error. " + "Omit this field to create new Report.")
    @Override
    public ReportId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the report creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id. Use 'assignReportToTenant' to change the Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @ApiModelProperty(position = 4, required = true, value = "Unique Report Name in scope of Tenant", example = "A4B72CCDFF33")
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(position = 5, value = "", example = "")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ApiModelProperty(position = 6, value = "", example = "")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty(position = 7, value = "", example = "")
    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    @ApiModelProperty(position = 8, value = "", example = "")
    public List<String> getTelemetryKeys() {
        return telemetryKeys;
    }

    public void setTelemetryKeys(List<String> telemetryKeys) {
        this.telemetryKeys = telemetryKeys;
    }

    @ApiModelProperty(position = 9, value = "List of assigned customers with their info.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public Set<ShortCustomerInfo> getAssignedCustomers() {
        return assignedCustomers;
    }

    public void setAssignedCustomers(Set<ShortCustomerInfo> assignedCustomers) {
        this.assignedCustomers = assignedCustomers;
    }

    public boolean isAssignedToCustomer(CustomerId customerId) {
        return this.assignedCustomers != null && this.assignedCustomers.contains(new ShortCustomerInfo(customerId, null));
    }

    public ShortCustomerInfo getAssignedCustomerInfo(CustomerId customerId) {
        if (this.assignedCustomers != null) {
            for (ShortCustomerInfo customerInfo : this.assignedCustomers) {
                if (customerInfo.getCustomerId().equals(customerId)) {
                    return customerInfo;
                }
            }
        }
        return null;
    }

    public boolean addAssignedCustomer(Customer customer) {
        ShortCustomerInfo customerInfo = customer.toShortCustomerInfo();
        if (this.assignedCustomers != null && this.assignedCustomers.contains(customerInfo)) {
            return false;
        } else {
            if (this.assignedCustomers == null) {
                this.assignedCustomers = new HashSet<>();
            }
            this.assignedCustomers.add(customerInfo);
            return true;
        }
    }

    public boolean updateAssignedCustomer(Customer customer) {
        ShortCustomerInfo customerInfo = customer.toShortCustomerInfo();
        if (this.assignedCustomers != null && this.assignedCustomers.contains(customerInfo)) {
            this.assignedCustomers.remove(customerInfo);
            this.assignedCustomers.add(customerInfo);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeAssignedCustomer(Customer customer) {
        ShortCustomerInfo customerInfo = customer.toShortCustomerInfo();
        if (this.assignedCustomers != null && this.assignedCustomers.contains(customerInfo)) {
            this.assignedCustomers.remove(customerInfo);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReportInfo [tenantId=");
        builder.append(tenantId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", type=");
        builder.append(type);
        builder.append(", description=");
        builder.append(description);
        builder.append(", devices=");
        builder.append(devices);
        builder.append(", telemetryKeys=");
        builder.append(telemetryKeys);
        builder.append(", assignedCustomers=");
        builder.append(assignedCustomers);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

}
