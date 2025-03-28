
package org.thingsboard.server.transport.lwm2m.security.sql;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.leshan.client.object.Security;
import org.eclipse.leshan.core.util.Hex;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.thingsboard.server.common.data.device.credentials.lwm2m.LwM2MDeviceCredentials;
import org.thingsboard.server.common.data.device.credentials.lwm2m.RPKClientCredential;
import org.thingsboard.server.common.data.device.profile.Lwm2mDeviceProfileTransportConfiguration;
import org.thingsboard.server.transport.lwm2m.security.AbstractSecurityLwM2MIntegrationTest;

import javax.servlet.http.HttpServletResponse;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static org.eclipse.leshan.client.object.Security.rpk;
import static org.eclipse.leshan.client.object.Security.rpkBootstrap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.thingsboard.server.common.data.device.credentials.lwm2m.LwM2MSecurityMode.RPK;
import static org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.LwM2MClientState.ON_REGISTRATION_SUCCESS;
import static org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.LwM2MProfileBootstrapConfigType.BOTH;
import static org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.LwM2MProfileBootstrapConfigType.NONE;

public class RpkLwM2MIntegrationTest extends AbstractSecurityLwM2MIntegrationTest {

    //Lwm2m only
    @Test
    public void testWithRpkConnectLwm2mSuccess() throws Exception {
        String clientEndpoint = CLIENT_ENDPOINT_RPK;
        X509Certificate certificate = clientX509CertTrust;
        PrivateKey privateKey = clientPrivateKeyFromCertTrust;
        RPKClientCredential clientCredentials = new RPKClientCredential();
        clientCredentials.setEndpoint(clientEndpoint);
        clientCredentials.setKey(Base64.encodeBase64String(certificate.getPublicKey().getEncoded()));
        Security securityBs = rpk(SECURE_URI,
                shortServerId,
                certificate.getPublicKey().getEncoded(),
                privateKey.getEncoded(),
                serverX509Cert.getPublicKey().getEncoded());
        Lwm2mDeviceProfileTransportConfiguration transportConfiguration = getTransportConfiguration(OBSERVE_ATTRIBUTES_WITHOUT_PARAMS, getBootstrapServerCredentialsSecure(RPK, NONE));
        LwM2MDeviceCredentials deviceCredentials = getDeviceCredentialsSecure(clientCredentials, privateKey, certificate, RPK, false);
        this.basicTestConnection(securityBs,
                deviceCredentials,
                COAP_CONFIG,
                clientEndpoint,
                transportConfiguration,
                "await on client state (Rpk_Lwm2m)",
                expectedStatusesRegistrationLwm2mSuccess,
                false,
                ON_REGISTRATION_SUCCESS,
                true);
    }

    @Test
    public void testWithRpkValidationPublicKeyBase64format_BAD_REQUEST() throws Exception {
        String clientEndpoint = CLIENT_ENDPOINT_RPK + "BadPublicKey";
        X509Certificate certificate = clientX509CertTrust;
        PrivateKey privateKey = clientPrivateKeyFromCertTrust;
        RPKClientCredential clientCredentials = new RPKClientCredential();
        clientCredentials.setEndpoint(clientEndpoint);
        clientCredentials.setKey(Hex.encodeHexString(certificate.getPublicKey().getEncoded()));
        Lwm2mDeviceProfileTransportConfiguration transportConfiguration = getTransportConfiguration(OBSERVE_ATTRIBUTES_WITHOUT_PARAMS, getBootstrapServerCredentialsSecure(RPK, NONE));
        LwM2MDeviceCredentials deviceCredentials = getDeviceCredentialsSecure(clientCredentials, privateKey, certificate, RPK, false);
        createDeviceProfile(transportConfiguration);
        MvcResult result = createDeviceWithMvcResult(deviceCredentials, clientEndpoint);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, result.getResponse().getStatus());
        String msgExpected = "LwM2M client RPK key must be in standard [RFC7250] and support only EC algorithm and then encoded to Base64 format!";
        assertTrue(result.getResponse().getContentAsString().contains(msgExpected));
    }

    @Test
    public void testWithRpkValidationPrivateKeyBase64format_BAD_REQUEST() throws Exception {
        String clientEndpoint = CLIENT_ENDPOINT_RPK + "BadPrivateKey";
        X509Certificate certificate = clientX509CertTrust;
        PrivateKey privateKey = clientPrivateKeyFromCertTrust;
        RPKClientCredential clientCredentials = new RPKClientCredential();
        clientCredentials.setEndpoint(clientEndpoint);
        clientCredentials.setKey(Base64.encodeBase64String(certificate.getPublicKey().getEncoded()));
        Lwm2mDeviceProfileTransportConfiguration transportConfiguration = getTransportConfiguration(OBSERVE_ATTRIBUTES_WITHOUT_PARAMS, getBootstrapServerCredentialsSecure(RPK, NONE));
        LwM2MDeviceCredentials deviceCredentials = getDeviceCredentialsSecure(clientCredentials, privateKey, certificate, RPK, true);
        createDeviceProfile(transportConfiguration);
        MvcResult result = createDeviceWithMvcResult(deviceCredentials, clientEndpoint);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, result.getResponse().getStatus());
        String msgExpected = "Bootstrap server client RPK secret key must be in PKCS#8 format (DER encoding, standard [RFC5958]) and then encoded to Base64 format!";
        assertTrue(result.getResponse().getContentAsString().contains(msgExpected));
    }

    // Bootstrap + Lwm2m
    @Test
    public void testWithRpkConnectBsSuccess_UpdateTwoSectionsBootstrapAndLm2m_ConnectLwm2mSuccess() throws Exception {
        String clientEndpoint = CLIENT_ENDPOINT_RPK_BS;
        X509Certificate certificate = clientX509CertTrust;
        PrivateKey privateKey = clientPrivateKeyFromCertTrust;
        RPKClientCredential clientCredentials = new RPKClientCredential();
        clientCredentials.setEndpoint(clientEndpoint);
        clientCredentials.setKey(Base64.encodeBase64String(certificate.getPublicKey().getEncoded()));
        Security securityBs = rpkBootstrap(SECURE_URI_BS,
                certificate.getPublicKey().getEncoded(),
                privateKey.getEncoded(),
                serverX509CertBs.getPublicKey().getEncoded());
        Lwm2mDeviceProfileTransportConfiguration transportConfiguration = getTransportConfiguration(OBSERVE_ATTRIBUTES_WITHOUT_PARAMS, getBootstrapServerCredentialsSecure(RPK, BOTH));
        LwM2MDeviceCredentials deviceCredentials = getDeviceCredentialsSecure(clientCredentials, clientPrivateKeyFromCertTrust, certificate, RPK, false);
        this.basicTestConnection(securityBs,
                deviceCredentials,
                COAP_CONFIG_BS,
                clientEndpoint,
                transportConfiguration,
                "await on client state (RpkBS two section)",
                expectedStatusesRegistrationBsSuccess,
                true,
                ON_REGISTRATION_SUCCESS,
                true);
    }
}
