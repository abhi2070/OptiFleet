package org.thingsboard.server.service.integration;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.sync.ie.importing.csv.BulkImportColumnType;
import org.thingsboard.server.dao.integration.IntegrationService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.integration.TbIntegrationService;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.sync.ie.importing.csv.AbstractBulkImportService;

import java.util.Map;
import java.util.Optional;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class IntegrationBulkImportService extends AbstractBulkImportService<Integration> {
    private final IntegrationService integrationService;
    private final TbIntegrationService tbIntegrationService;

    @Override
    protected void setEntityFields(Integration entity, Map<BulkImportColumnType, String> fields) {
        ObjectNode additionalInfo = getOrCreateAdditionalInfoObj(entity);
        fields.forEach((columnType, value) -> {
            switch (columnType) {
                case NAME:
                    entity.setName(value);
                    break;
                case TYPE:
                    entity.setType(value);
                    break;
                case ENABLE_INTEGRATION:
                    additionalInfo.set("Enable integration", BooleanNode.valueOf(Boolean.parseBoolean(value)));
                    break;
                case DEBUG_MODE:
                    additionalInfo.set("Debug mode", BooleanNode.valueOf(Boolean.parseBoolean(value)));
                    break;
                case ALLOW_CREATE_DEVICES_OR_ASSETS:
                    additionalInfo.set("Allow create devices or assets", BooleanNode.valueOf(Boolean.parseBoolean(value)));
                    break;
            }
        });
        entity.setAdditionalInfo(additionalInfo);
    }

    @Override
    @SneakyThrows
    protected Integration saveEntity(SecurityUser user, Integration entity, Map<BulkImportColumnType, String> fields) {
        return tbIntegrationService.save(entity, user);
    }

    @Override
    protected Integration findOrCreateEntity(TenantId tenantId, String name) {
        return Optional.ofNullable(integrationService.findIntegrationByTenantIdAndName(tenantId, name))
                .orElseGet(Integration::new);
    }

    @Override
    protected void setOwners(Integration entity, SecurityUser user) {
        entity.setTenantId(user.getTenantId());
        entity.setCustomerId(user.getCustomerId());
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.INTEGRATION;
    }
}
