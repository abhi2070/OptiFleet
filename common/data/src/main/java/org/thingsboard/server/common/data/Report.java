package org.thingsboard.server.common.data;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.ReportId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import javax.validation.Valid;
import java.util.*;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Report extends ReportInfo implements ExportableEntity<ReportId> {

    private static final long serialVersionUID = -3004579925090663691L;

    @Getter @Setter
    private ReportId externalId;

    public Report() {
        super();
    }

    public Report(ReportId id) {
        super(id);
    }

    public Report(ReportInfo reportInfo) {
        super(reportInfo);
    }

    public Report(Report report) {
        super(report);
        this.externalId = report.getExternalId();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Report [tenantId=");
        builder.append(getTenantId());
        builder.append(", name=");
        builder.append(getName());
        builder.append("]");
        return builder.toString();
    }
}
