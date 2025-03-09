
package org.thingsboard.server.service.edge.rpc.constructor.tenant;

import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.gen.edge.v1.TenantProfileUpdateMsg;
import org.thingsboard.server.gen.edge.v1.TenantUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.DataDecodingEncodingService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.edge.rpc.utils.EdgeVersionUtils;

@Component
@TbCoreComponent
public class TenantMsgConstructorV1 implements TenantMsgConstructor {

    @Autowired
    private DataDecodingEncodingService dataDecodingEncodingService;

    @Override
    public TenantUpdateMsg constructTenantUpdateMsg(UpdateMsgType msgType, Tenant tenant) {
        TenantUpdateMsg.Builder builder = TenantUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(tenant.getId().getId().getMostSignificantBits())
                .setIdLSB(tenant.getId().getId().getLeastSignificantBits())
                .setTitle(tenant.getTitle())
                .setProfileIdMSB(tenant.getTenantProfileId().getId().getMostSignificantBits())
                .setProfileIdLSB(tenant.getTenantProfileId().getId().getLeastSignificantBits())
                .setRegion(tenant.getRegion());
        if (tenant.getCountry() != null) {
            builder.setCountry(tenant.getCountry());
        }
        if (tenant.getState() != null) {
            builder.setState(tenant.getState());
        }
        if (tenant.getCity() != null) {
            builder.setCity(tenant.getCity());
        }
        if (tenant.getAddress() != null) {
            builder.setAddress(tenant.getAddress());
        }
        if (tenant.getAddress2() != null) {
            builder.setAddress2(tenant.getAddress2());
        }
        if (tenant.getZip() != null) {
            builder.setZip(tenant.getZip());
        }
        if (tenant.getPhone() != null) {
            builder.setPhone(tenant.getPhone());
        }
        if (tenant.getEmail() != null) {
            builder.setEmail(tenant.getEmail());
        }
        if (tenant.getAdditionalInfo() != null) {
            builder.setAdditionalInfo(JacksonUtil.toString(tenant.getAdditionalInfo()));
        }
        return builder.build();
    }

    @Override
    public TenantProfileUpdateMsg constructTenantProfileUpdateMsg(UpdateMsgType msgType, TenantProfile tenantProfile, EdgeVersion edgeVersion) {
        ByteString profileData = EdgeVersionUtils.isEdgeVersionOlderThan(edgeVersion, EdgeVersion.V_3_6_2) ?
                ByteString.empty() : ByteString.copyFrom(dataDecodingEncodingService.encode(tenantProfile.getProfileData()));
        TenantProfileUpdateMsg.Builder builder = TenantProfileUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(tenantProfile.getId().getId().getMostSignificantBits())
                .setIdLSB(tenantProfile.getId().getId().getLeastSignificantBits())
                .setName(tenantProfile.getName())
                .setDefault(tenantProfile.isDefault())
                .setIsolatedRuleChain(tenantProfile.isIsolatedTbRuleEngine())
                .setProfileDataBytes(profileData);
        if (tenantProfile.getDescription() != null) {
            builder.setDescription(tenantProfile.getDescription());
        }
        return builder.build();
    }
}
