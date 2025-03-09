
package org.thingsboard.server.service.edge.rpc.constructor.ota;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.OtaPackage;
import org.thingsboard.server.gen.edge.v1.OtaPackageUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class OtaPackageMsgConstructorV2 extends BaseOtaPackageMsgConstructor {

    @Override
    public OtaPackageUpdateMsg constructOtaPackageUpdatedMsg(UpdateMsgType msgType, OtaPackage otaPackage) {
        return OtaPackageUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(otaPackage))
                .setIdMSB(otaPackage.getId().getId().getMostSignificantBits())
                .setIdLSB(otaPackage.getId().getId().getLeastSignificantBits()).build();
    }
}
