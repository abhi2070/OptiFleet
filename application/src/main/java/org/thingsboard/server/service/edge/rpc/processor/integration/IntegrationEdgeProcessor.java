package org.thingsboard.server.service.edge.rpc.processor.integration;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.edge.EdgeEventType;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.IntegrationId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.integration.Integration;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.integration.BaseIntegrationService;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.IntegrationUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.integration.IntegrationMsgConstructor;

import java.util.UUID;

@Slf4j
public abstract class IntegrationEdgeProcessor extends BaseIntegrationProcessor implements IntegrationProcessor {

    @Override
    public ListenableFuture<Void> processIntegrationMsgFromEdge(TenantId tenantId, Edge edge, IntegrationUpdateMsg integrationUpdateMsg) {
        log.trace("[{}] executing processIntegrationMsgFromEdge [{}] from edge [{}]", tenantId, integrationUpdateMsg, edge.getId());
        IntegrationId integrationId = new IntegrationId(new UUID(integrationUpdateMsg.getIdMSB(), integrationUpdateMsg.getIdLSB()));
        try {
            edgeSynchronizationManager.getEdgeId().set(edge.getId());

            switch (integrationUpdateMsg.getMsgType()) {
                case ENTITY_CREATED_RPC_MESSAGE:
                case ENTITY_UPDATED_RPC_MESSAGE:
                    saveOrUpdateIntegration(tenantId, integrationId, integrationUpdateMsg, edge);
                    return Futures.immediateFuture(null);
                case ENTITY_DELETED_RPC_MESSAGE:
                    Integration integrationToDelete = integrationService.findIntegrationById(tenantId, integrationId);
                    if (integrationToDelete != null) {
                        integrationService.unassignIntegrationFromEdge(tenantId, integrationId, edge.getId());
                    }
                    return Futures.immediateFuture(null);
                case UNRECOGNIZED:
                default:
                    return handleUnsupportedMsgType(integrationUpdateMsg.getMsgType());
            }
        } catch (DataValidationException e) {
            if (e.getMessage().contains("limit reached")) {
                log.warn("[{}] Number of allowed integration violated {}", tenantId, integrationUpdateMsg, e);
                return Futures.immediateFuture(null);
            } else {
                return Futures.immediateFailedFuture(e);
            }
        } finally {
            edgeSynchronizationManager.getEdgeId().remove();
        }
    }

    private void saveOrUpdateIntegration(TenantId tenantId, IntegrationId integrationId, IntegrationUpdateMsg integrationUpdateMsg, Edge edge) {
        Pair<Boolean, Boolean> resultPair = super.saveOrUpdateIntegration(tenantId, integrationId, integrationUpdateMsg);
        Boolean created = resultPair.getFirst();
        if (created) {
            createRelationFromEdge(tenantId, edge.getId(), integrationId);
            pushIntegrationCreatedEventToRuleEngine(tenantId,  integrationId);
            integrationService.assignIntegrationToEdge(tenantId, integrationId, edge.getId());
        }
        Boolean integrationNameUpdated = resultPair.getSecond();
        if (integrationNameUpdated) {
            saveEdgeEvent(tenantId, edge.getId(), EdgeEventType.INTEGRATION, EdgeEventActionType.UPDATED, integrationId, null);
        }
    }

    private void pushIntegrationCreatedEventToRuleEngine(TenantId tenantId, IntegrationId integrationId) {
        try {
            Integration integration = integrationService.findIntegrationById(tenantId, integrationId);
            String integrationAsString = JacksonUtil.toString(integration);

        } catch (Exception e) {
            log.warn("[{}][{}] Failed to push integration action to rule engine: {}", tenantId, integrationId, TbMsgType.ENTITY_CREATED.name(), e);
        }
    }

    @Override
    public DownlinkMsg convertIntegrationEventToDownlink(EdgeEvent edgeEvent, EdgeId edgeId, EdgeVersion edgeVersion) {
        IntegrationId integrationId = new IntegrationId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (edgeEvent.getAction()) {
            case ADDED:
            case UPDATED:
            case ASSIGNED_TO_EDGE:
            case ASSIGNED_TO_CUSTOMER:
            case UNASSIGNED_FROM_CUSTOMER:
                Integration integration = integrationService.findIntegrationById(edgeEvent.getTenantId(), integrationId);
                if (integration != null && !BaseIntegrationService.TB_SERVICE_QUEUE.equals(integration.getType())) {
                    UpdateMsgType msgType = getUpdateMsgType(edgeEvent.getAction());
                    IntegrationUpdateMsg integrationUpdateMsg = ((IntegrationMsgConstructor)
                            integrationMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructIntegrationUpdatedMsg(msgType, integration);
                    DownlinkMsg.Builder builder = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addIntegrationUpdateMsg(integrationUpdateMsg);

                    downlinkMsg = builder.build();
                }
                break;
            case DELETED:
            case UNASSIGNED_FROM_EDGE:
                IntegrationUpdateMsg integrationUpdateMsg = ((IntegrationMsgConstructor)
                        integrationMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructIntegrationDeleteMsg(integrationId);
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addIntegrationUpdateMsg(integrationUpdateMsg)
                        .build();
                break;
        }
        return downlinkMsg;
    }

}
