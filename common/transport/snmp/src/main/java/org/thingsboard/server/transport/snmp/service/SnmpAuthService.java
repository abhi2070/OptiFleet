
package org.thingsboard.server.transport.snmp.service;

import lombok.RequiredArgsConstructor;
import org.snmp4j.AbstractTarget;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.device.data.SnmpDeviceTransportConfiguration;
import org.thingsboard.server.common.data.device.profile.SnmpDeviceProfileTransportConfiguration;
import org.thingsboard.server.common.data.transport.snmp.SnmpProtocolVersion;
import org.thingsboard.server.queue.util.TbSnmpTransportComponent;
import org.thingsboard.server.transport.snmp.session.DeviceSessionContext;

import java.util.Optional;

@Service
@TbSnmpTransportComponent
@RequiredArgsConstructor
public class SnmpAuthService {
    private final SnmpTransportService snmpTransportService;

    @Value("${transport.snmp.underlying_protocol}")
    private String snmpUnderlyingProtocol;

    public Target setUpSnmpTarget(SnmpDeviceProfileTransportConfiguration profileTransportConfig, SnmpDeviceTransportConfiguration deviceTransportConfig) {
        AbstractTarget target;

        SnmpProtocolVersion protocolVersion = deviceTransportConfig.getProtocolVersion();
        switch (protocolVersion) {
            case V1:
                CommunityTarget communityTargetV1 = new CommunityTarget();
                communityTargetV1.setSecurityModel(SecurityModel.SECURITY_MODEL_SNMPv1);
                communityTargetV1.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
                communityTargetV1.setCommunity(new OctetString(deviceTransportConfig.getCommunity()));
                target = communityTargetV1;
                break;
            case V2C:
                CommunityTarget communityTargetV2 = new CommunityTarget();
                communityTargetV2.setSecurityModel(SecurityModel.SECURITY_MODEL_SNMPv2c);
                communityTargetV2.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
                communityTargetV2.setCommunity(new OctetString(deviceTransportConfig.getCommunity()));
                target = communityTargetV2;
                break;
            case V3:
                OctetString username = new OctetString(deviceTransportConfig.getUsername());
                OctetString securityName = new OctetString(deviceTransportConfig.getSecurityName());
                OctetString engineId = new OctetString(deviceTransportConfig.getEngineId());

                OID authenticationProtocol = new OID(deviceTransportConfig.getAuthenticationProtocol().getOid());
                OID privacyProtocol = new OID(deviceTransportConfig.getPrivacyProtocol().getOid());
                OctetString authenticationPassphrase = new OctetString(deviceTransportConfig.getAuthenticationPassphrase());
                authenticationPassphrase = new OctetString(SecurityProtocols.getInstance().passwordToKey(authenticationProtocol, authenticationPassphrase, engineId.getValue()));
                OctetString privacyPassphrase = new OctetString(deviceTransportConfig.getPrivacyPassphrase());
                privacyPassphrase = new OctetString(SecurityProtocols.getInstance().passwordToKey(privacyProtocol, authenticationProtocol, privacyPassphrase, engineId.getValue()));

                USM usm = snmpTransportService.getSnmp().getUSM();
                if (usm.hasUser(engineId, securityName)) {
                    usm.removeAllUsers(username, engineId);
                }
                usm.addLocalizedUser(
                        engineId.getValue(), username,
                        authenticationProtocol, authenticationPassphrase.getValue(),
                        privacyProtocol, privacyPassphrase.getValue()
                );

                UserTarget userTarget = new UserTarget();
                userTarget.setSecurityName(securityName);
                userTarget.setAuthoritativeEngineID(engineId.getValue());
                userTarget.setSecurityModel(SecurityModel.SECURITY_MODEL_USM);
                userTarget.setSecurityLevel(SecurityLevel.AUTH_PRIV);
                target = userTarget;
                break;
            default:
                throw new UnsupportedOperationException("SNMP protocol version " + protocolVersion + " is not supported");
        }

        Address address = GenericAddress.parse(snmpUnderlyingProtocol + ":" + deviceTransportConfig.getHost() + "/" + deviceTransportConfig.getPort());
        target.setAddress(Optional.ofNullable(address).orElseThrow(() -> new IllegalArgumentException("Address of the SNMP device is invalid")));
        target.setTimeout(profileTransportConfig.getTimeoutMs());
        target.setRetries(profileTransportConfig.getRetries());
        target.setVersion(protocolVersion.getCode());

        return target;
    }

    public void cleanUpSnmpAuthInfo(DeviceSessionContext sessionContext) {
        SnmpDeviceTransportConfiguration deviceTransportConfiguration = sessionContext.getDeviceTransportConfiguration();
        if (deviceTransportConfiguration.getProtocolVersion() == SnmpProtocolVersion.V3) {
            OctetString username = new OctetString(deviceTransportConfiguration.getUsername());
            OctetString engineId = new OctetString(deviceTransportConfiguration.getEngineId());
            snmpTransportService.getSnmp().getUSM().removeAllUsers(username, engineId);
        }
    }

}
