package org.thingsboard.server.common.data;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.ReportId;
import org.thingsboard.server.common.data.id.TenantId;
import java.io.Serializable;
import java.util.UUID;

@Data
@Slf4j
public class ReportIdInfo implements Serializable, HasTenantId {

    private static final long serialVersionUID = 2233745129677581815L;

    private final TenantId tenantId;
    private final CustomerId customerId;
    private final ReportId reportId;

    public ReportIdInfo(UUID tenantId, UUID customerId, UUID reportId) {
        this.tenantId = new TenantId(tenantId);
        this.customerId = customerId != null ? new CustomerId(customerId) : null;
        this.reportId = new ReportId(reportId);
    }
}
