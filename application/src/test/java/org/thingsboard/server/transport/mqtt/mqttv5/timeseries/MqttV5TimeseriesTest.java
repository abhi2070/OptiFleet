
package org.thingsboard.server.transport.mqtt.mqttv5.timeseries;

import org.junit.Before;
import org.junit.Test;
import org.thingsboard.server.dao.service.DaoSqlTest;
import org.thingsboard.server.transport.mqtt.MqttTestConfigProperties;

@DaoSqlTest
public class MqttV5TimeseriesTest extends AbstractMqttV5TimeseriesTest {

    @Before
    public void beforeTest() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Post Telemetry device")
                .build();
        processBeforeTest(configProperties);
    }

    @Test
    public void testTimeseriesMqttV5SimpleClientUpload() throws Exception {
        processTimeseriesMqttV5UploadTest();
    }

}
