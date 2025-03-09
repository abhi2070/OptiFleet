
package org.thingsboard.monitoring.config.transport;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "monitoring.transports.http.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "monitoring.transports.http")
public class HttpTransportMonitoringConfig extends TransportMonitoringConfig {

    @Override
    public TransportType getTransportType() {
        return TransportType.HTTP;
    }

}
