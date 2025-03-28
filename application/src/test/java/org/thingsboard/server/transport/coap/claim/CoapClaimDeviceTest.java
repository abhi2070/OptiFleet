
package org.thingsboard.server.transport.coap.claim;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.thingsboard.server.common.data.ClaimRequest;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.msg.session.FeatureType;
import org.thingsboard.server.dao.device.claim.ClaimResponse;
import org.thingsboard.server.dao.device.claim.ClaimResult;
import org.thingsboard.server.dao.service.DaoSqlTest;
import org.thingsboard.server.transport.coap.AbstractCoapIntegrationTest;
import org.thingsboard.server.transport.coap.CoapTestClient;
import org.thingsboard.server.transport.coap.CoapTestConfigProperties;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DaoSqlTest
public class CoapClaimDeviceTest extends AbstractCoapIntegrationTest {

    protected static final String CUSTOMER_USER_PASSWORD = "customerUser123!";

    protected User customerAdmin;
    protected Customer savedCustomer;

    @Before
    public void beforeTest() throws Exception {
        CoapTestConfigProperties configProperties = CoapTestConfigProperties.builder()
                .deviceName("Test Claim device")
                .build();
        processBeforeTest(configProperties);
        createCustomerAndUser();
    }

    protected void createCustomerAndUser() throws Exception {
        Customer customer = new Customer();
        customer.setTenantId(tenantId);
        customer.setTitle("Test Claiming Customer");
        savedCustomer = doPost("/api/customer", customer, Customer.class);
        assertNotNull(savedCustomer);
        assertEquals(tenantId, savedCustomer.getTenantId());

        User user = new User();
        user.setAuthority(Authority.CUSTOMER_USER);
        user.setTenantId(tenantId);
        user.setCustomerId(savedCustomer.getId());
        user.setEmail("customer@thingsboard.org");

        customerAdmin = createUser(user, CUSTOMER_USER_PASSWORD);
        assertNotNull(customerAdmin);
        assertEquals(customerAdmin.getCustomerId(), savedCustomer.getId());
    }

    @After
    public void afterTest() throws Exception {
        processAfterTest();
    }

    @Test
    public void testClaimingDevice() throws Exception {
        processTestClaimingDevice(false);
    }

    @Test
    public void testClaimingDeviceWithoutSecretAndDuration() throws Exception {
        processTestClaimingDevice(true);
    }

    protected void processTestClaimingDevice(boolean emptyPayload) throws Exception {
        log.warn("[testClaimingDevice] Device: {}, Transport type: {}", savedDevice.getName(), savedDevice.getType());
        client = new CoapTestClient(accessToken, FeatureType.CLAIM);
        byte[] payloadBytes;
        byte[] failurePayloadBytes;
        if (emptyPayload) {
            payloadBytes = "{}".getBytes();
            failurePayloadBytes = "{\"durationMs\":1}".getBytes();
        } else {
            payloadBytes = "{\"secretKey\":\"value\", \"durationMs\":60000}".getBytes();
            failurePayloadBytes = "{\"secretKey\":\"value\", \"durationMs\":1}".getBytes();
        }
        validateClaimResponse(emptyPayload, client, payloadBytes, failurePayloadBytes);
    }

    protected void validateClaimResponse(boolean emptyPayload, CoapTestClient client, byte[] payloadBytes, byte[] failurePayloadBytes) throws Exception {
        postClaimRequest(client, failurePayloadBytes);

        loginUser(customerAdmin.getName(), CUSTOMER_USER_PASSWORD);
        ClaimRequest claimRequest;
        if (!emptyPayload) {
            claimRequest = new ClaimRequest("value");
        } else {
            claimRequest = new ClaimRequest(null);
        }

        ClaimResponse claimResponse = doExecuteWithRetriesAndInterval(
                () -> doPostClaimAsync("/api/customer/device/" + savedDevice.getName() + "/claim", claimRequest, ClaimResponse.class, status().isBadRequest()),
                100,
                200
        );

        assertEquals(claimResponse, ClaimResponse.FAILURE);

        postClaimRequest(client, payloadBytes);

        ClaimResult claimResult = doExecuteWithRetriesAndInterval(
                () -> doPostClaimAsync("/api/customer/device/" + savedDevice.getName() + "/claim", claimRequest, ClaimResult.class, status().isOk()),
                100,
                200
        );
        assertEquals(claimResult.getResponse(), ClaimResponse.SUCCESS);
        Device claimedDevice = claimResult.getDevice();
        assertNotNull(claimedDevice);
        assertNotNull(claimedDevice.getCustomerId());
        assertEquals(customerAdmin.getCustomerId(), claimedDevice.getCustomerId());

        claimResponse = doPostClaimAsync("/api/customer/device/" + savedDevice.getName() + "/claim", claimRequest, ClaimResponse.class, status().isBadRequest());
        assertEquals(claimResponse, ClaimResponse.CLAIMED);
    }

    private void postClaimRequest(CoapTestClient client, byte[] payload) throws IOException, ConnectorException {
        CoapResponse coapResponse = client.postMethod(payload);
        assertEquals(CoAP.ResponseCode.CREATED, coapResponse.getCode());
    }

}
