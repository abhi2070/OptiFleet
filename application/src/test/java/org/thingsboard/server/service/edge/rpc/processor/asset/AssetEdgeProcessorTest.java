
package org.thingsboard.server.service.edge.rpc.processor.asset;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;

@SpringBootTest(classes = {AssetEdgeProcessorV1.class})
class AssetEdgeProcessorTest extends AbstractAssetProcessorTest {

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void testAssetProfileDefaultFields_notSendToEdgeOlder3_6_0IfNotAssigned(EdgeVersion edgeVersion, long expectedDashboardIdMSB, long expectedDashboardIdLSB,
                                                                                   long expectedRuleChainIdMSB, long expectedRuleChainIdLSB) {
        updateAssetProfileDefaultFields(expectedDashboardIdMSB, expectedDashboardIdLSB, expectedRuleChainIdMSB, expectedRuleChainIdLSB);

        edgeEvent.setEntityId(assetId.getId());

        DownlinkMsg downlinkMsg = assetProcessorV1.convertAssetEventToDownlink(edgeEvent, edgeId, edgeVersion);

        verify(downlinkMsg, expectedDashboardIdMSB, expectedDashboardIdLSB, expectedRuleChainIdMSB, expectedRuleChainIdLSB);
    }
}
