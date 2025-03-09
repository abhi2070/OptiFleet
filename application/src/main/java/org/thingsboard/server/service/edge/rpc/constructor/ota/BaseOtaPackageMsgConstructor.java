
package org.thingsboard.server.service.edge.rpc.constructor.ota;

import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.gen.edge.v1.OtaPackageUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;

public abstract class BaseOtaPackageMsgConstructor implements OtaPackageMsgConstructor {

    @Override
    public OtaPackageUpdateMsg constructOtaPackageDeleteMsg(OtaPackageId otaPackageId) {
        return OtaPackageUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(otaPackageId.getId().getMostSignificantBits())
                .setIdLSB(otaPackageId.getId().getLeastSignificantBits()).build();
    }
}
