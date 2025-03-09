
package org.thingsboard.server.service.edge.rpc.processor.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.TenantProfileUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.tenant.TenantMsgConstructor;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;

@Slf4j
@Component
@TbCoreComponent
public class TenantProfileEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg convertTenantProfileEventToDownlink(EdgeEvent edgeEvent, EdgeVersion edgeVersion) {
        TenantProfileId tenantProfileId = new TenantProfileId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        if (EdgeEventActionType.UPDATED.equals(edgeEvent.getAction())) {
            TenantProfile tenantProfile = tenantProfileService.findTenantProfileById(edgeEvent.getTenantId(), tenantProfileId);
            if (tenantProfile != null) {
                UpdateMsgType msgType = getUpdateMsgType(edgeEvent.getAction());
                TenantProfileUpdateMsg tenantProfileUpdateMsg = ((TenantMsgConstructor)
                        tenantMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion))
                        .constructTenantProfileUpdateMsg(msgType, tenantProfile, edgeVersion);
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addTenantProfileUpdateMsg(tenantProfileUpdateMsg)
                        .build();
            }
        }
        return downlinkMsg;
    }
}
