package org.thingsboard.server.service.edge.rpc.processor.roles;

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
import org.thingsboard.server.common.data.id.RolesId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.roles.BaseRolesService;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.RolesUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.roles.RolesMsgConstructor;


import java.util.UUID;

@Slf4j
public abstract  class RolesEdgeProcessor extends BaseRolesProcessor implements RolesProcessor {

    @Override
    public ListenableFuture<Void> processRolesMsgFromEdge(TenantId tenantId, Edge edge, RolesUpdateMsg rolesUpdateMsg) {
        log.trace("[{}] executing processRolesMsgFromEdge [{}] from edge [{}]", tenantId, rolesUpdateMsg, edge.getId());
        RolesId rolesId = new RolesId(new UUID(rolesUpdateMsg.getIdMSB(), rolesUpdateMsg.getIdLSB()));
        try {
            edgeSynchronizationManager.getEdgeId().set(edge.getId());

            switch (rolesUpdateMsg.getMsgType()) {
                case ENTITY_CREATED_RPC_MESSAGE:
                case ENTITY_UPDATED_RPC_MESSAGE:
                    saveOrUpdateRoles(tenantId, rolesId, rolesUpdateMsg, edge);
                    return Futures.immediateFuture(null);
                case ENTITY_DELETED_RPC_MESSAGE:
                    Roles rolesToDelete = rolesService.findRolesById(tenantId, rolesId);
                    if (rolesToDelete != null) {
                        rolesService.unassignRolesFromEdge(tenantId, rolesId, edge.getId());
                    }
                    return Futures.immediateFuture(null);
                case UNRECOGNIZED:
                default:
                    return handleUnsupportedMsgType(rolesUpdateMsg.getMsgType());
            }
        } catch (DataValidationException e) {
            if (e.getMessage().contains("limit reached")) {
                log.warn("[{}] Number of allowed roles violated {}", tenantId, rolesUpdateMsg, e);
                return Futures.immediateFuture(null);
            } else {
                return Futures.immediateFailedFuture(e);
            }
        } finally {
            edgeSynchronizationManager.getEdgeId().remove();
        }
    }

    private void saveOrUpdateRoles(TenantId tenantId, RolesId rolesId, RolesUpdateMsg rolesUpdateMsg, Edge edge) {
        Pair<Boolean, Boolean> resultPair = super.saveOrUpdateRoles(tenantId, rolesId, rolesUpdateMsg);
        Boolean created = resultPair.getFirst();
        if (created) {
            createRelationFromEdge(tenantId, edge.getId(), rolesId);
            pushRolesCreatedEventToRuleEngine(tenantId, edge,  rolesId);
            rolesService.assignRolesToEdge(tenantId, rolesId, edge.getId());
        }
        Boolean rolesNameUpdated = resultPair.getSecond();
        if (rolesNameUpdated) {
            saveEdgeEvent(tenantId, edge.getId(), EdgeEventType.ROLES, EdgeEventActionType.UPDATED, rolesId, null);
        }
    }

    private void pushRolesCreatedEventToRuleEngine(TenantId tenantId,Edge edge, RolesId rolesId) {
        try {
            Roles roles = rolesService.findRolesById(tenantId, rolesId);
            String rolesAsString = JacksonUtil.toString(roles);
            TbMsgMetaData msgMetaData = getEdgeActionTbMsgMetaData(edge, roles.getCustomerId());
            pushEntityEventToRuleEngine(tenantId, rolesId, roles.getCustomerId(), TbMsgType.ENTITY_CREATED, rolesAsString, msgMetaData);

        } catch (Exception e) {
            log.warn("[{}][{}] Failed to push roles action to rule engine: {}", tenantId, rolesId, TbMsgType.ENTITY_CREATED.name(), e);
        }
    }

    @Override
    public DownlinkMsg convertRolesEventToDownlink(EdgeEvent edgeEvent, EdgeId edgeId, EdgeVersion edgeVersion) {
        RolesId rolesId = new RolesId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (edgeEvent.getAction()) {
            case ADDED:
            case UPDATED:
            case ASSIGNED_TO_EDGE:
            case ASSIGNED_TO_CUSTOMER:
            case UNASSIGNED_FROM_CUSTOMER:
                Roles roles = rolesService.findRolesById(edgeEvent.getTenantId(), rolesId);
                if (roles != null && !BaseRolesService.TB_SERVICE_QUEUE.equals(roles.getType())) {
                    UpdateMsgType msgType = getUpdateMsgType(edgeEvent.getAction());
                    RolesUpdateMsg rolesUpdateMsg = ((RolesMsgConstructor)
                            rolesMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructRolesUpdatedMsg(msgType, roles);
                    DownlinkMsg.Builder builder = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addRolesUpdateMsg(rolesUpdateMsg);

                    downlinkMsg = builder.build();
                }
                break;
            case DELETED:
            case UNASSIGNED_FROM_EDGE:
                RolesUpdateMsg rolesUpdateMsg = ((RolesMsgConstructor)
                        rolesMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructRolesDeleteMsg(rolesId);
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addRolesUpdateMsg(rolesUpdateMsg)
                        .build();
                break;
        }
        return downlinkMsg;
    }

}
