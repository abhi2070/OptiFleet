
package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JavaType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.DashboardInfo;
import org.thingsboard.server.common.data.ShortCustomerInfo;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.id.DashboardId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.UUID;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = ModelConstants.DASHBOARD_TABLE_NAME)
public class DashboardInfoEntity extends BaseSqlEntity<DashboardInfo> {

    private static final JavaType assignedCustomersType =
            JacksonUtil.constructCollectionType(HashSet.class, ShortCustomerInfo.class);

    @Column(name = ModelConstants.DASHBOARD_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = ModelConstants.DASHBOARD_TITLE_PROPERTY)
    private String title;

    @Column(name = ModelConstants.DASHBOARD_IMAGE_PROPERTY)
    private String image;

    @Column(name = ModelConstants.DASHBOARD_ASSIGNED_CUSTOMERS_PROPERTY)
    private String assignedCustomers;

    @Column(name = ModelConstants.DASHBOARD_MOBILE_HIDE_PROPERTY)
    private boolean mobileHide;

    @Column(name = ModelConstants.DASHBOARD_MOBILE_ORDER_PROPERTY)
    private Integer mobileOrder;

    public DashboardInfoEntity() {
        super();
    }

    public DashboardInfoEntity(DashboardInfo dashboardInfo) {
        if (dashboardInfo.getId() != null) {
            this.setUuid(dashboardInfo.getId().getId());
        }
        this.setCreatedTime(dashboardInfo.getCreatedTime());
        if (dashboardInfo.getTenantId() != null) {
            this.tenantId = dashboardInfo.getTenantId().getId();
        }
        this.title = dashboardInfo.getTitle();
        this.image = dashboardInfo.getImage();
        if (dashboardInfo.getAssignedCustomers() != null) {
            try {
                this.assignedCustomers = JacksonUtil.toString(dashboardInfo.getAssignedCustomers());
            } catch (IllegalArgumentException e) {
                log.error("Unable to serialize assigned customers to string!", e);
            }
        }
        this.mobileHide = dashboardInfo.isMobileHide();
        this.mobileOrder = dashboardInfo.getMobileOrder();
    }

    @Override
    public DashboardInfo toData() {
        DashboardInfo dashboardInfo = new DashboardInfo(new DashboardId(this.getUuid()));
        dashboardInfo.setCreatedTime(createdTime);
        if (tenantId != null) {
            dashboardInfo.setTenantId(TenantId.fromUUID(tenantId));
        }
        dashboardInfo.setTitle(title);
        dashboardInfo.setImage(image);
        if (!StringUtils.isEmpty(assignedCustomers)) {
            try {
                dashboardInfo.setAssignedCustomers(JacksonUtil.fromString(assignedCustomers, assignedCustomersType));
            } catch (IllegalArgumentException e) {
                log.warn("Unable to parse assigned customers!", e);
            }
        }
        dashboardInfo.setMobileHide(mobileHide);
        dashboardInfo.setMobileOrder(mobileOrder);
        return dashboardInfo;
    }

}
