
package org.thingsboard.server.service.edge.rpc.constructor.ota;

import com.google.protobuf.ByteString;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.OtaPackage;
import org.thingsboard.server.gen.edge.v1.OtaPackageUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class OtaPackageMsgConstructorV1 extends BaseOtaPackageMsgConstructor {

    @Override
    public OtaPackageUpdateMsg constructOtaPackageUpdatedMsg(UpdateMsgType msgType, OtaPackage otaPackage) {
        OtaPackageUpdateMsg.Builder builder = OtaPackageUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(otaPackage.getId().getId().getMostSignificantBits())
                .setIdLSB(otaPackage.getId().getId().getLeastSignificantBits())
                .setType(otaPackage.getType().name())
                .setTitle(otaPackage.getTitle())
                .setVersion(otaPackage.getVersion())
                .setTag(otaPackage.getTag());

        if (otaPackage.getDeviceProfileId() != null) {
            builder.setDeviceProfileIdMSB(otaPackage.getDeviceProfileId().getId().getMostSignificantBits())
                    .setDeviceProfileIdLSB(otaPackage.getDeviceProfileId().getId().getLeastSignificantBits());
        }

        if (otaPackage.getUrl() != null) {
            builder.setUrl(otaPackage.getUrl());
        }
        if (otaPackage.getAdditionalInfo() != null) {
            builder.setAdditionalInfo(JacksonUtil.toString(otaPackage.getAdditionalInfo()));
        }
        if (otaPackage.getFileName() != null) {
            builder.setFileName(otaPackage.getFileName());
        }
        if (otaPackage.getContentType() != null) {
            builder.setContentType(otaPackage.getContentType());
        }
        if (otaPackage.getChecksumAlgorithm() != null) {
            builder.setChecksumAlgorithm(otaPackage.getChecksumAlgorithm().name());
        }
        if (otaPackage.getChecksum() != null) {
            builder.setChecksum(otaPackage.getChecksum());
        }
        if (otaPackage.getDataSize() != null) {
            builder.setDataSize(otaPackage.getDataSize());
        }
        if (otaPackage.getData() != null) {
            builder.setData(ByteString.copyFrom(otaPackage.getData().array()));
        }
        return builder.build();
    }
}
