
package org.thingsboard.server.service.edge.rpc.processor.resource;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.edge.EdgeEventType;
import org.thingsboard.server.common.data.id.TbResourceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.ResourceUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.resource.ResourceMsgConstructor;

import java.util.UUID;

@Slf4j
public abstract class ResourceEdgeProcessor extends BaseResourceProcessor implements ResourceProcessor {

    @Override
    public ListenableFuture<Void> processResourceMsgFromEdge(TenantId tenantId, Edge edge, ResourceUpdateMsg resourceUpdateMsg) {
        TbResourceId tbResourceId = new TbResourceId(new UUID(resourceUpdateMsg.getIdMSB(), resourceUpdateMsg.getIdLSB()));
        try {
            edgeSynchronizationManager.getEdgeId().set(edge.getId());

            switch (resourceUpdateMsg.getMsgType()) {
                case ENTITY_CREATED_RPC_MESSAGE:
                case ENTITY_UPDATED_RPC_MESSAGE:
                    boolean resourceKeyUpdated = super.saveOrUpdateTbResource(tenantId, tbResourceId, resourceUpdateMsg);
                    if (resourceKeyUpdated) {
                        saveEdgeEvent(tenantId, edge.getId(), EdgeEventType.TB_RESOURCE, EdgeEventActionType.UPDATED, tbResourceId, null);
                    }
                    break;
                case ENTITY_DELETED_RPC_MESSAGE:
                case UNRECOGNIZED:
                    return handleUnsupportedMsgType(resourceUpdateMsg.getMsgType());
            }
        } catch (DataValidationException e) {
            if (e.getMessage().contains("exceeds the maximum")) {
                log.warn("[{}] Resource data size has been exhausted {}", tenantId, resourceUpdateMsg, e);
                return Futures.immediateFuture(null);
            } else {
                return Futures.immediateFailedFuture(e);
            }
        } finally {
            edgeSynchronizationManager.getEdgeId().remove();
        }
        return Futures.immediateFuture(null);
    }

    @Override
    public DownlinkMsg convertResourceEventToDownlink(EdgeEvent edgeEvent, EdgeVersion edgeVersion) {
        TbResourceId tbResourceId = new TbResourceId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (edgeEvent.getAction()) {
            case ADDED:
            case UPDATED:
                TbResource tbResource = resourceService.findResourceById(edgeEvent.getTenantId(), tbResourceId);
                if (tbResource != null) {
                    UpdateMsgType msgType = getUpdateMsgType(edgeEvent.getAction());
                    ResourceUpdateMsg resourceUpdateMsg = ((ResourceMsgConstructor)
                            resourceMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructResourceUpdatedMsg(msgType, tbResource);
                    downlinkMsg = resourceUpdateMsg != null ? DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addResourceUpdateMsg(resourceUpdateMsg)
                            .build() : null;
                }
                break;
            case DELETED:
                ResourceUpdateMsg resourceUpdateMsg = ((ResourceMsgConstructor)
                        resourceMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructResourceDeleteMsg(tbResourceId);
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addResourceUpdateMsg(resourceUpdateMsg)
                        .build();
                break;
        }
        return downlinkMsg;
    }
}
