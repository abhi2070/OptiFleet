package org.thingsboard.server.common.data.integration;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.IntegrationId;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class IntegrationInfo extends Integration {

    private static final long serialVersionUID = -4094528227011066194L;

    private String customerTitle;
    private boolean customerIsPublic;


    public IntegrationInfo() {
        super();
    }

    public IntegrationInfo(IntegrationId integrationId) {
        super(integrationId);
    }

    //, String integrationProfileName
    public IntegrationInfo(Integration integration, String customerTitle, boolean customerIsPublic) {
        super(integration);
        this.customerTitle = customerTitle;
        this.customerIsPublic = customerIsPublic;
    }
}
