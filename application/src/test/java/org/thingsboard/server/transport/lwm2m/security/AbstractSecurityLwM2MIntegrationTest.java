
package org.thingsboard.server.transport.lwm2m.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.leshan.client.object.Security;
import org.eclipse.leshan.core.ResponseCode;
import org.eclipse.leshan.core.util.Hex;
import org.junit.Assert;
import org.springframework.test.web.servlet.MvcResult;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.device.credentials.lwm2m.LwM2MBootstrapClientCredentials;
import org.thingsboard.server.common.data.device.credentials.lwm2m.LwM2MClientCredential;
import org.thingsboard.server.common.data.device.credentials.lwm2m.LwM2MDeviceCredentials;
import org.thingsboard.server.common.data.device.credentials.lwm2m.LwM2MSecurityMode;
import org.thingsboard.server.common.data.device.credentials.lwm2m.NoSecBootstrapClientCredential;
import org.thingsboard.server.common.data.device.credentials.lwm2m.PSKBootstrapClientCredential;
import org.thingsboard.server.common.data.device.credentials.lwm2m.PSKClientCredential;
import org.thingsboard.server.common.data.device.credentials.lwm2m.RPKBootstrapClientCredential;
import org.thingsboard.server.common.data.device.credentials.lwm2m.X509BootstrapClientCredential;
import org.thingsboard.server.common.data.device.profile.Lwm2mDeviceProfileTransportConfiguration;
import org.thingsboard.server.common.data.device.profile.lwm2m.bootstrap.AbstractLwM2MBootstrapServerCredential;
import org.thingsboard.server.common.data.device.profile.lwm2m.bootstrap.LwM2MBootstrapServerCredential;
import org.thingsboard.server.common.data.device.profile.lwm2m.bootstrap.PSKLwM2MBootstrapServerCredential;
import org.thingsboard.server.common.data.device.profile.lwm2m.bootstrap.RPKLwM2MBootstrapServerCredential;
import org.thingsboard.server.common.data.device.profile.lwm2m.bootstrap.X509LwM2MBootstrapServerCredential;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.common.data.security.DeviceCredentialsType;
import org.thingsboard.server.dao.service.DaoSqlTest;
import org.thingsboard.server.transport.lwm2m.AbstractLwM2MIntegrationTest;
import org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.LwM2MClientState;
import org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.LwM2MProfileBootstrapConfigType;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.eclipse.leshan.client.object.Security.noSecBootstap;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.LwM2MClientState.ON_DEREGISTRATION_STARTED;
import static org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.LwM2MClientState.ON_DEREGISTRATION_SUCCESS;
import static org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.LwM2MClientState.ON_REGISTRATION_STARTED;
import static org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.LwM2MClientState.ON_REGISTRATION_SUCCESS;
import static org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.LwM2MClientState.ON_UPDATE_SUCCESS;
import static org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.OBJECT_ID_1;
import static org.thingsboard.server.transport.lwm2m.Lwm2mTestHelper.RESOURCE_ID_9;

@DaoSqlTest
@Slf4j
public abstract class AbstractSecurityLwM2MIntegrationTest extends AbstractLwM2MIntegrationTest {

    protected final String CREDENTIALS_PATH = "lwm2m/credentials/";                              // client public key or id used for PSK
    //             Get keys PSK
    protected final String CLIENT_PSK_IDENTITY = "SOME_PSK_ID";                                  // client public key or id used for PSK
    protected final String CLIENT_PSK_IDENTITY_BS = "SOME_PSK_ID_BS";                            // client public key or id used for PSK
    protected final String CLIENT_PSK_KEY = "73656372657450534b73656372657450";                  // client private/secret key used for PSK

    // Server
    protected static final String SERVER_JKS_FOR_TEST = "lwm2mserver";
    protected static final String SERVER_STORE_PWD = "server_ks_password";
    protected static final String SERVER_CERT_ALIAS = "server";
    protected static final String SERVER_CERT_ALIAS_BS = "bootstrap";
    protected final X509Certificate serverX509Cert;                                               // server certificate signed by rootCA
    protected final X509Certificate serverX509CertBs;                                             // serverBs certificate signed by rootCA
    protected final PublicKey serverPublicKeyFromCert;                                            // server public key used for RPK
    protected final PublicKey serverPublicKeyFromCertBs;                                          // serverBs public key used for RPK

    // Client
    protected static final String CLIENT_ENDPOINT_NO_SEC = "LwNoSec00000000";
    protected static final String CLIENT_ENDPOINT_NO_SEC_BS = "LwNoSecBs00000000";
    protected static final String CLIENT_ENDPOINT_PSK = "LwPsk00000000";
    protected static final String CLIENT_ENDPOINT_PSK_BS = "LwPskBs00000000";
    protected static final String CLIENT_ENDPOINT_RPK = "LwRpk00000000";
    protected static final String CLIENT_ENDPOINT_RPK_BS = "LwRpkBs00000000";
    protected static final String CLIENT_ENDPOINT_X509_TRUST = "LwX50900000000";
    protected static final String CLIENT_ENDPOINT_X509_TRUST_NO = "LwX509TrustNo";
    protected static final String CLIENT_JKS_FOR_TEST = "lwm2mclient";
    protected static final String CLIENT_STORE_PWD = "client_ks_password";
    protected static final String CLIENT_ALIAS_CERT_TRUST = "client_alias_00000000";
    protected static final String CLIENT_ALIAS_CERT_TRUST_NO = "client_alias_trust_no";

    protected final X509Certificate clientX509CertTrust;                                        // client certificate signed by intermediate, rootCA with a good CN ("host name")
    protected final PrivateKey clientPrivateKeyFromCertTrust;                                   // client private key used for X509 and RPK
    protected final X509Certificate clientX509CertTrustNo;                                      // client certificate signed by intermediate, rootCA with a good CN ("host name")
    protected final PrivateKey clientPrivateKeyFromCertTrustNo;                                 // client private key used for X509 and RPK
    private final String[] RESOURCES_SECURITY = new String[]{"1.xml", "2.xml", "3.xml", "5.xml", "9.xml"};


    private final LwM2MBootstrapClientCredentials defaultBootstrapCredentials;


    public AbstractSecurityLwM2MIntegrationTest() {
        // create client credentials
        setResources(this.RESOURCES_SECURITY);
        try {
            // Get certificates from key store
            char[] clientKeyStorePwd = CLIENT_STORE_PWD.toCharArray();
            KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (InputStream clientKeyStoreFile = this.getClass().getClassLoader().getResourceAsStream(CREDENTIALS_PATH + CLIENT_JKS_FOR_TEST + ".jks")) {
                clientKeyStore.load(clientKeyStoreFile, clientKeyStorePwd);
            }
            // Trust
            clientPrivateKeyFromCertTrust = (PrivateKey) clientKeyStore.getKey(CLIENT_ALIAS_CERT_TRUST, clientKeyStorePwd);
            clientX509CertTrust = (X509Certificate) clientKeyStore.getCertificate(CLIENT_ALIAS_CERT_TRUST);
            // No trust
            clientPrivateKeyFromCertTrustNo = (PrivateKey) clientKeyStore.getKey(CLIENT_ALIAS_CERT_TRUST_NO, clientKeyStorePwd);
            clientX509CertTrustNo = (X509Certificate) clientKeyStore.getCertificate(CLIENT_ALIAS_CERT_TRUST_NO);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        // create server credentials
        try {
            // Get certificates from key store
            char[] serverKeyStorePwd = SERVER_STORE_PWD.toCharArray();
            KeyStore serverKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (InputStream serverKeyStoreFile = this.getClass().getClassLoader().getResourceAsStream(CREDENTIALS_PATH + SERVER_JKS_FOR_TEST + ".jks")) {
                serverKeyStore.load(serverKeyStoreFile, serverKeyStorePwd);
            }

            serverX509Cert = (X509Certificate) serverKeyStore.getCertificate(SERVER_CERT_ALIAS);
            serverPublicKeyFromCert = serverX509Cert.getPublicKey();
            serverX509CertBs = (X509Certificate) serverKeyStore.getCertificate(SERVER_CERT_ALIAS_BS);
            serverPublicKeyFromCertBs = serverX509CertBs.getPublicKey();

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        defaultBootstrapCredentials = new LwM2MBootstrapClientCredentials();

        NoSecBootstrapClientCredential serverCredentials = new NoSecBootstrapClientCredential();

        defaultBootstrapCredentials.setBootstrapServer(serverCredentials);
        defaultBootstrapCredentials.setLwm2mServer(serverCredentials);
    }

    public void basicTestConnectionBefore(String clientEndpoint,
                                          String awaitAlias,
                                          LwM2MProfileBootstrapConfigType type,
                                          Set<LwM2MClientState> expectedStatuses,
                                          LwM2MClientState finishState) throws Exception {
        Lwm2mDeviceProfileTransportConfiguration transportConfiguration = getTransportConfiguration(OBSERVE_ATTRIBUTES_WITHOUT_PARAMS, getBootstrapServerCredentialsNoSec(type));
        LwM2MDeviceCredentials deviceCredentials = getDeviceCredentialsNoSec(createNoSecClientCredentials(clientEndpoint));
        this.basicTestConnection(noSecBootstap(URI_BS),
                deviceCredentials,
                COAP_CONFIG_BS,
                clientEndpoint,
                transportConfiguration,
                awaitAlias,
                expectedStatuses,
                true,
                finishState,
                false);
    }

    protected void basicTestConnection(Security security,
                                       LwM2MDeviceCredentials deviceCredentials,
                                       Configuration coapConfig,
                                       String endpoint,
                                       Lwm2mDeviceProfileTransportConfiguration transportConfiguration,
                                       String awaitAlias,
                                       Set<LwM2MClientState> expectedStatuses,
                                       boolean isBootstrap,
                                       LwM2MClientState finishState,
                                       boolean isStartLw) throws Exception {
        createDeviceProfile(transportConfiguration);
        final Device device = createDevice(deviceCredentials, endpoint);
        device.getId().getId().toString();
        createNewClient(security, coapConfig, true, endpoint, isBootstrap, null);
        lwM2MTestClient.start(isStartLw);
        awaitObserveReadAll(0, isBootstrap, device.getId().getId().toString());
        await(awaitAlias)
                .atMost(40, TimeUnit.SECONDS)
                .until(() -> {
                    log.warn("basicTestConnection started -> finishState: [{}] states: {}", finishState, lwM2MTestClient.getClientStates());
                    return lwM2MTestClient.getClientStates().contains(finishState) || lwM2MTestClient.getClientStates().contains(ON_REGISTRATION_STARTED);
                });
        await(awaitAlias)
                .atMost(40, TimeUnit.SECONDS)
                .until(() -> {
                    log.warn("basicTestConnection -> finishState: [{}] states: {}", finishState, lwM2MTestClient.getClientStates());
                    return lwM2MTestClient.getClientStates().contains(finishState) || lwM2MTestClient.getClientStates().contains(ON_UPDATE_SUCCESS);
                });
        Assert.assertTrue(lwM2MTestClient.getClientStates().containsAll(expectedStatuses));
    }


    public void basicTestConnectionBootstrapRequestTriggerBefore(String clientEndpoint, String awaitAlias, LwM2MProfileBootstrapConfigType type) throws Exception {
        Lwm2mDeviceProfileTransportConfiguration transportConfiguration = getTransportConfiguration(OBSERVE_ATTRIBUTES_WITHOUT_PARAMS, getBootstrapServerCredentialsNoSec(type));
        LwM2MDeviceCredentials deviceCredentials = getDeviceCredentialsNoSec(createNoSecClientCredentials(clientEndpoint));
        this.basicTestConnectionBootstrapRequestTrigger(
                SECURITY_NO_SEC,
                deviceCredentials,
                COAP_CONFIG,
                clientEndpoint,
                transportConfiguration,
                awaitAlias,
                expectedStatusesRegistrationLwm2mSuccess,
                expectedStatusesRegistrationBsSuccess,
                false,
                SECURITY_NO_SEC_BS);
    }

    private void basicTestConnectionBootstrapRequestTrigger(Security security,
                                                            LwM2MDeviceCredentials deviceCredentials,
                                                            Configuration coapConfig,
                                                            String endpoint,
                                                            Lwm2mDeviceProfileTransportConfiguration transportConfiguration,
                                                            String awaitAlias,
                                                            Set<LwM2MClientState> expectedStatusesLwm2m,
                                                            Set<LwM2MClientState> expectedStatusesBs,
                                                            boolean isBootstrap,
                                                            Security securityBs) throws Exception {

        createDeviceProfile(transportConfiguration);
        final Device device = createDevice(deviceCredentials, endpoint);
        String deviceIdStr = device.getId().getId().toString();
        createNewClient(security, coapConfig, true, endpoint, isBootstrap, securityBs);
        lwM2MTestClient.start(true);
        awaitObserveReadAll(0, isBootstrap, deviceIdStr);
        await(awaitAlias)
                .atMost(40, TimeUnit.SECONDS)
                .until(() -> {
                    log.warn("basicTest First Connection started -> finishState: [{}] states: {}", ON_REGISTRATION_SUCCESS, lwM2MTestClient.getClientStates());
                    return lwM2MTestClient.getClientStates().contains(ON_REGISTRATION_SUCCESS) || lwM2MTestClient.getClientStates().contains(ON_REGISTRATION_STARTED);
                });
        await(awaitAlias)
                .atMost(40, TimeUnit.SECONDS)
                .until(() -> {
                    log.warn("basicTest First  Connection -> finishState: [{}] states: {}", ON_REGISTRATION_SUCCESS, lwM2MTestClient.getClientStates());
                    return lwM2MTestClient.getClientStates().contains(ON_REGISTRATION_SUCCESS) || lwM2MTestClient.getClientStates().contains(ON_UPDATE_SUCCESS);
                });
        Assert.assertTrue(lwM2MTestClient.getClientStates().containsAll(expectedStatusesLwm2m));

        String executedPath = "/" + OBJECT_ID_1 + "_" +  lwM2MTestClient.getLeshanClient().getObjectTree().getModel().getObjectModel(OBJECT_ID_1).version
                + "/0/" + RESOURCE_ID_9;
        lwM2MTestClient.setClientStates(new HashSet<>());
        String actualResult = sendRPCSecurityExecuteById(executedPath, deviceIdStr, endpoint);
        ObjectNode rpcActualResult = JacksonUtil.fromString(actualResult, ObjectNode.class);
        if (!(rpcActualResult.get("result").asText().equals(ResponseCode.CHANGED.getName()))) {
            actualResult = sendRPCSecurityExecuteById(executedPath, deviceIdStr, endpoint);
            rpcActualResult = JacksonUtil.fromString(actualResult, ObjectNode.class);
        }
        assertEquals(ResponseCode.CHANGED.getName(), rpcActualResult.get("result").asText());
        expectedStatusesBs.add(ON_DEREGISTRATION_STARTED);
        expectedStatusesBs.add(ON_DEREGISTRATION_SUCCESS);
        await(awaitAlias)
                .atMost(40, TimeUnit.SECONDS)
                .until(() -> {
                    log.warn("basicTestConnection started -> finishState: [{}] states: {}", ON_REGISTRATION_SUCCESS, lwM2MTestClient.getClientStates());
                    return lwM2MTestClient.getClientStates().contains(ON_REGISTRATION_SUCCESS) || lwM2MTestClient.getClientStates().contains(ON_REGISTRATION_STARTED);
                });
        await(awaitAlias)
                .atMost(40, TimeUnit.SECONDS)
                .until(() -> {
                    log.warn("basicTestConnection -> finishState: [{}] states: {}", ON_REGISTRATION_SUCCESS, lwM2MTestClient.getClientStates());
                    return lwM2MTestClient.getClientStates().contains(ON_REGISTRATION_SUCCESS) || lwM2MTestClient.getClientStates().contains(ON_UPDATE_SUCCESS);
                });
        Assert.assertTrue(lwM2MTestClient.getClientStates().containsAll(expectedStatusesBs));
    }

    protected List<LwM2MBootstrapServerCredential> getBootstrapServerCredentialsSecure(LwM2MSecurityMode mode, LwM2MProfileBootstrapConfigType bootstrapConfigType) {
        List<LwM2MBootstrapServerCredential> bootstrap = new ArrayList<>();
        switch (bootstrapConfigType) {
            case BOTH:
                bootstrap.add(getBootstrapServerCredential(mode, false));
                bootstrap.add(getBootstrapServerCredential(mode, true));
                break;
            case BOOTSTRAP_ONLY:
                bootstrap.add(getBootstrapServerCredential(mode, true));
                break;
            case LWM2M_ONLY:
                bootstrap.add(getBootstrapServerCredential(mode, false));
                break;
            case NONE:
        }
        return bootstrap;
    }

    private AbstractLwM2MBootstrapServerCredential getBootstrapServerCredential(LwM2MSecurityMode mode, boolean isBootstrap) {
        AbstractLwM2MBootstrapServerCredential bootstrapServerCredential;
        switch (mode) {
            case PSK:
                bootstrapServerCredential = new PSKLwM2MBootstrapServerCredential();
                bootstrapServerCredential.setServerPublicKey("");
                break;
            case RPK:
                bootstrapServerCredential = new RPKLwM2MBootstrapServerCredential();
                if (isBootstrap) {
                    bootstrapServerCredential.setServerPublicKey(Base64.encodeBase64String(serverPublicKeyFromCertBs.getEncoded()));
                } else {
                    bootstrapServerCredential.setServerPublicKey(Base64.encodeBase64String(serverPublicKeyFromCert.getEncoded()));
                }
                break;
            case X509:
                bootstrapServerCredential = new X509LwM2MBootstrapServerCredential();
                try {
                    if (isBootstrap) {
                        bootstrapServerCredential.setServerPublicKey(Base64.encodeBase64String(serverX509CertBs.getEncoded()));
                    } else {
                        bootstrapServerCredential.setServerPublicKey(Base64.encodeBase64String(serverX509Cert.getEncoded()));
                    }
                } catch (CertificateEncodingException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
        bootstrapServerCredential.setShortServerId(isBootstrap ? shortServerIdBs : shortServerId);
        bootstrapServerCredential.setBootstrapServerIs(isBootstrap);
        bootstrapServerCredential.setHost(isBootstrap ? hostBs : host);
        bootstrapServerCredential.setPort(isBootstrap ? securityPortBs : securityPort);
        return bootstrapServerCredential;
    }


    protected LwM2MDeviceCredentials getDeviceCredentialsSecure(LwM2MClientCredential clientCredentials,
                                                                PrivateKey privateKey,
                                                                X509Certificate certificate,
                                                                LwM2MSecurityMode mode,
                                                                boolean privateKeyIsBad) {
        LwM2MDeviceCredentials credentials = new LwM2MDeviceCredentials();
        credentials.setClient(clientCredentials);
        LwM2MBootstrapClientCredentials bootstrapCredentials;
        switch (mode) {
            case PSK:
                bootstrapCredentials = getBootstrapClientCredentialsPsk(clientCredentials);
                break;
            case RPK:
                bootstrapCredentials = getBootstrapClientCredentialsRpk(certificate, privateKey, privateKeyIsBad);
                break;
            case X509:
                bootstrapCredentials = getBootstrapClientCredentialsX509(certificate, privateKey, privateKeyIsBad);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
        credentials.setBootstrap(bootstrapCredentials);
        return credentials;
    }

    private LwM2MBootstrapClientCredentials getBootstrapClientCredentialsPsk(LwM2MClientCredential clientCredentials) {
        LwM2MBootstrapClientCredentials bootstrapCredentials = new LwM2MBootstrapClientCredentials();
        PSKBootstrapClientCredential serverCredentials = new PSKBootstrapClientCredential();
        if (clientCredentials != null) {
            serverCredentials.setClientSecretKey(((PSKClientCredential) clientCredentials).getKey());
            serverCredentials.setClientPublicKeyOrId(((PSKClientCredential) clientCredentials).getIdentity());
        }
        bootstrapCredentials.setBootstrapServer(serverCredentials);
        bootstrapCredentials.setLwm2mServer(serverCredentials);
        return bootstrapCredentials;
    }

    private LwM2MBootstrapClientCredentials getBootstrapClientCredentialsRpk(X509Certificate certificate, PrivateKey privateKey, boolean privateKeyIsBad) {
        LwM2MBootstrapClientCredentials bootstrapCredentials = new LwM2MBootstrapClientCredentials();
        RPKBootstrapClientCredential serverCredentials = new RPKBootstrapClientCredential();
        if (certificate != null && certificate.getPublicKey() != null && privateKey != null) {
            serverCredentials.setClientPublicKeyOrId(Base64.encodeBase64String(certificate.getPublicKey().getEncoded()));
            if (privateKeyIsBad) {
                serverCredentials.setClientSecretKey(Hex.encodeHexString(privateKey.getEncoded()));
            } else {
                serverCredentials.setClientSecretKey(Base64.encodeBase64String(privateKey.getEncoded()));

            }
        }
        bootstrapCredentials.setBootstrapServer(serverCredentials);
        bootstrapCredentials.setLwm2mServer(serverCredentials);
        return bootstrapCredentials;
    }

    private LwM2MBootstrapClientCredentials getBootstrapClientCredentialsX509(X509Certificate certificate, PrivateKey privateKey, boolean privateKeyIsBad) {
        LwM2MBootstrapClientCredentials bootstrapCredentials = new LwM2MBootstrapClientCredentials();
        X509BootstrapClientCredential serverCredentials = new X509BootstrapClientCredential();
        if (certificate != null) {
            try {
                serverCredentials.setClientPublicKeyOrId(Base64.encodeBase64String(certificate.getEncoded()));
                if (privateKeyIsBad) {
                    serverCredentials.setClientSecretKey(Hex.encodeHexString(privateKey.getEncoded()));
                } else {
                    serverCredentials.setClientSecretKey(Base64.encodeBase64String(privateKey.getEncoded()));
                }
            } catch (CertificateEncodingException e) {
                log.error("Client`s certificate [{}] is bad. [{}]", certificate, e.getMessage());
            }
        }
        bootstrapCredentials.setBootstrapServer(serverCredentials);
        bootstrapCredentials.setLwm2mServer(serverCredentials);
        return bootstrapCredentials;
    }

    protected MvcResult createDeviceWithMvcResult(LwM2MDeviceCredentials credentials, String endpoint) throws Exception {
        Device device = new Device();
        device.setName(endpoint);
        device.setDeviceProfileId(deviceProfile.getId());
        device.setTenantId(tenantId);
        device = doPost("/api/device", device, Device.class);
        Assert.assertNotNull(device);

        DeviceCredentials deviceCredentials =
                doGet("/api/device/" + device.getId().getId().toString() + "/credentials", DeviceCredentials.class);
        Assert.assertEquals(device.getId(), deviceCredentials.getDeviceId());
        deviceCredentials.setCredentialsType(DeviceCredentialsType.LWM2M_CREDENTIALS);
        deviceCredentials.setCredentialsValue(JacksonUtil.toString(credentials));
        return doPost("/api/device/credentials", deviceCredentials).andReturn();
    }

    protected String sendRPCSecurityExecuteById(String path, String deviceId, String endpoint) throws Exception {
        log.info("endpoint1: [{}]", endpoint);


        String setRpcRequest = "{\"method\": \"Execute\", \"params\": {\"id\": \"" + path + "\"}}";
        return doPostAsync("/api/plugins/rpc/twoway/" + deviceId, setRpcRequest, String.class, status().isOk());
    }
}
