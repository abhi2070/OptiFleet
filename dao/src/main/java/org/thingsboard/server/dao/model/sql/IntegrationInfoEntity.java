package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.integration.IntegrationInfo;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class IntegrationInfoEntity extends AbstractIntegrationEntity<IntegrationInfo>{

    public static final Map<String,String> integrationInfoColumnMap = new HashMap<>();
    static {
        integrationInfoColumnMap.put("customerTitle", "c.title");
        integrationInfoColumnMap.put("integrationProfileName", "p.name");
    }

    private String customerTitle;
    private boolean customerIsPublic;
    private String integrationProfileName;

    public IntegrationInfoEntity() {
        super();
    }

    public IntegrationInfoEntity(IntegrationEntity integrationEntity,
                           String customerTitle,
                           Object customerAdditionalInfo) {
        super(integrationEntity);
        this.customerTitle = customerTitle;
        if (customerAdditionalInfo != null && ((JsonNode)customerAdditionalInfo).has("isPublic")) {
            this.customerIsPublic = ((JsonNode)customerAdditionalInfo).get("isPublic").asBoolean();
        } else {
            this.customerIsPublic = false;
        }
    }

    @Override
    public IntegrationInfo toData() {
        return new IntegrationInfo(super.toIntegration(), customerTitle, customerIsPublic);
    }

}
