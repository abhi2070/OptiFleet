
package org.thingsboard.server.service.edge.rpc.processor.ota;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EdgeUtils;
import org.thingsboard.server.common.data.OtaPackage;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.OtaPackageUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.constructor.ota.OtaPackageMsgConstructor;
import org.thingsboard.server.service.edge.rpc.processor.BaseEdgeProcessor;

@Component
@Slf4j
@TbCoreComponent
public class OtaPackageEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg convertOtaPackageEventToDownlink(EdgeEvent edgeEvent, EdgeVersion edgeVersion) {
        OtaPackageId otaPackageId = new OtaPackageId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (edgeEvent.getAction()) {
            case ADDED:
            case UPDATED:
                OtaPackage otaPackage = otaPackageService.findOtaPackageById(edgeEvent.getTenantId(), otaPackageId);
                if (otaPackage != null) {
                    UpdateMsgType msgType = getUpdateMsgType(edgeEvent.getAction());
                    OtaPackageUpdateMsg otaPackageUpdateMsg = ((OtaPackageMsgConstructor)
                            otaPackageMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructOtaPackageUpdatedMsg(msgType, otaPackage);
                    downlinkMsg = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addOtaPackageUpdateMsg(otaPackageUpdateMsg)
                            .build();
                }
                break;
            case DELETED:
                OtaPackageUpdateMsg otaPackageUpdateMsg = ((OtaPackageMsgConstructor)
                        otaPackageMsgConstructorFactory.getMsgConstructorByEdgeVersion(edgeVersion)).constructOtaPackageDeleteMsg(otaPackageId);
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addOtaPackageUpdateMsg(otaPackageUpdateMsg)
                        .build();
                break;
        }
        return downlinkMsg;
    }
}
