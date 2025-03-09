
package org.thingsboard.server.transport.mqtt.util.sparkplug;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SparkplugRpcRequestHeader {

    private String messageType;
    private String metricName;
    private Object value;

}
