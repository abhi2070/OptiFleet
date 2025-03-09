
package org.thingsboard.server.service.edge.rpc.processor.device;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.service.edge.rpc.processor.device.profile.DeviceProfileEdgeProcessorV1;


@SpringBootTest(classes = {DeviceProfileEdgeProcessorV1.class})
class DeviceProfileEdgeProcessorTest extends AbstractDeviceProcessorTest {

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void testDeviceProfileDefaultFields_notSendToEdgeOlder3_6_0IfNotAssigned(EdgeVersion edgeVersion, long expectedDashboardIdMSB, long expectedDashboardIdLSB,
                                                                                    long expectedRuleChainIdMSB, long expectedRuleChainIdLSB) {
        updateDeviceProfileDefaultFields(expectedDashboardIdMSB, expectedDashboardIdLSB, expectedRuleChainIdMSB, expectedRuleChainIdLSB);

        edgeEvent.setEntityId(deviceProfileId.getId());

        DownlinkMsg downlinkMsg = deviceProfileProcessorV1.convertDeviceProfileEventToDownlink(edgeEvent, edgeId, edgeVersion);

        verify(downlinkMsg, expectedDashboardIdMSB, expectedDashboardIdLSB, expectedRuleChainIdMSB, expectedRuleChainIdLSB);
    }
}
