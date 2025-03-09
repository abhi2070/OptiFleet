package org.thingsboard.server.service.edge.rpc.processor.integration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.gen.edge.v1.IntegrationUpdateMsg;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;


@Slf4j
public abstract class BaseIntegrationProcessor extends BaseEdgeProcessor {

    protected Pair<Boolean, Boolean> saveOrUpdateIntegration(TenantId tenantId, IntegrationId integrationId, IntegrationUpdateMsg integrationUpdateMsg) {
        boolean created = false;
        boolean integrationNameUpdated = false;
        integrationCreationLock.lock();
        try {
            Integration integration = constructIntegrationFromUpdateMsg(tenantId, integrationId, integrationUpdateMsg);
            if (integration == null) {
                throw new RuntimeException("[{" + tenantId + "}] integrationUpdateMsg {" + integrationUpdateMsg + " } cannot be converted to integration");
            }
            Integration integrationById = integrationService.findIntegrationById(tenantId, integrationId);
            if (integrationById == null) {
                created = true;
                integration.setId(null);
            } else {
                integration.setId(integrationId);
            }
            String integrationName = integration.getName();
            Integration integrationByName = integrationService.findIntegrationByTenantIdAndName(tenantId, integrationName);
            if (integrationByName != null && !integrationByName.getId().equals(integrationId)) {
                integrationName = integrationName + "_" + StringUtils.randomAlphanumeric(15);
                log.warn("[{}] Integration with name {} already exists. Renaming integration name to {}",
                        tenantId, integration.getName(), integrationName);
                integrationNameUpdated = true;
            }
            integration.setName(integrationName);

            integrationValidator.validate(integration, Integration::getTenantId);
            if (created) {
                integration.setId(integrationId);
            }
            integrationService.saveIntegration(integration, true);
        } catch (Exception e) {
            log.error("[{}] Failed to process integration update msg [{}]", tenantId, integrationUpdateMsg, e);
            throw e;
        } finally {
            integrationCreationLock.unlock();
        }
        return Pair.of(created, integrationNameUpdated);
    }

    protected abstract Integration constructIntegrationFromUpdateMsg(TenantId tenantId, IntegrationId integrationId, IntegrationUpdateMsg integrationUpdateMsg);
}
