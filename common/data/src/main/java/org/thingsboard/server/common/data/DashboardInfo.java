
package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@ApiModel
public class DashboardInfo extends BaseData<DashboardId> implements HasName, HasTenantId, HasTitle, HasImage {

    private static final long serialVersionUID = -9080404114760433799L;

    private TenantId tenantId;
    @NoXss
    @Length(fieldName = "title")
    private String title;
    private String image;
    @Valid
    private Set<ShortCustomerInfo> assignedCustomers;
    private boolean mobileHide;
    private Integer mobileOrder;

    public DashboardInfo() {
        super();
    }

    public DashboardInfo(DashboardId id) {
        super(id);
    }

    public DashboardInfo(DashboardInfo dashboardInfo) {
        super(dashboardInfo);
        this.tenantId = dashboardInfo.getTenantId();
        this.title = dashboardInfo.getTitle();
        this.image = dashboardInfo.getImage();
        this.assignedCustomers = dashboardInfo.getAssignedCustomers();
        this.mobileHide = dashboardInfo.isMobileHide();
        this.mobileOrder = dashboardInfo.getMobileOrder();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the dashboard Id. " +
            "Specify existing dashboard Id to update the dashboard. " +
            "Referencing non-existing dashboard id will cause error. " +
            "Omit this field to create new dashboard.")
    @Override
    public DashboardId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the dashboard creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id. Tenant Id of the dashboard can't be changed.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @ApiModelProperty(position = 4, required = true, value = "Title of the dashboard.")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ApiModelProperty(position = 8, value = "Thumbnail picture for rendering of the dashboards in a grid view on mobile devices.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @ApiModelProperty(position = 5, value = "List of assigned customers with their info.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public Set<ShortCustomerInfo> getAssignedCustomers() {
        return assignedCustomers;
    }

    public void setAssignedCustomers(Set<ShortCustomerInfo> assignedCustomers) {
        this.assignedCustomers = assignedCustomers;
    }

    @ApiModelProperty(position = 6, value = "Hide dashboard from mobile devices. Useful if the dashboard is not designed for small screens.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public boolean isMobileHide() {
        return mobileHide;
    }

    public void setMobileHide(boolean mobileHide) {
        this.mobileHide = mobileHide;
    }

    @ApiModelProperty(position = 7, value = "Order on mobile devices. Useful to adjust sorting of the dashboards for mobile applications", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public Integer getMobileOrder() {
        return mobileOrder;
    }

    public void setMobileOrder(Integer mobileOrder) {
        this.mobileOrder = mobileOrder;
    }

    public boolean isAssignedToCustomer(CustomerId customerId) {
        return this.assignedCustomers != null && this.assignedCustomers.contains(new ShortCustomerInfo(customerId, null, false));
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

    @ApiModelProperty(position = 4, value = "Same as title of the dashboard. Read-only field. Update the 'title' to change the 'name' of the dashboard.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return title;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DashboardInfo that = (DashboardInfo) o;
        return mobileHide == that.mobileHide
                && Objects.equals(tenantId, that.tenantId)
                && Objects.equals(title, that.title)
                && Objects.equals(image, that.image)
                && Objects.equals(assignedCustomers, that.assignedCustomers)
                && Objects.equals(mobileOrder, that.mobileOrder);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DashboardInfo [tenantId=");
        builder.append(tenantId);
        builder.append(", title=");
        builder.append(title);
        builder.append("]");
        return builder.toString();
    }

}
