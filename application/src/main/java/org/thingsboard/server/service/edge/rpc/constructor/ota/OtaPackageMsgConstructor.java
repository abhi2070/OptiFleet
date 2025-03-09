
package org.thingsboard.server.service.edge.rpc.constructor.ota;

import org.thingsboard.server.common.data.OtaPackage;
import org.thingsboard.server.common.data.id.OtaPackageId;
import org.thingsboard.server.gen.edge.v1.OtaPackageUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface OtaPackageMsgConstructor extends MsgConstructor {

    OtaPackageUpdateMsg constructOtaPackageUpdatedMsg(UpdateMsgType msgType, OtaPackage otaPackage);

    OtaPackageUpdateMsg constructOtaPackageDeleteMsg(OtaPackageId otaPackageId);
}
