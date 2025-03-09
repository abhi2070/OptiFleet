
package org.thingsboard.server.service.edge.rpc.processor.telemetry;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class TelemetryEdgeProcessorTest {

    @Test
    public void testConvert_maxSizeLimit() throws Exception {
        EdgeEvent edgeEvent = new EdgeEvent();
        ObjectNode body = JacksonUtil.newObjectNode();
        body.put("value", StringUtils.randomAlphanumeric(10000));
        edgeEvent.setBody(body);
        DownlinkMsg downlinkMsg = new TelemetryEdgeProcessor().convertTelemetryEventToDownlink(edgeEvent);
        Assert.assertNull(downlinkMsg);
    }
}
