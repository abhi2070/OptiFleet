
package org.thingsboard.monitoring.config.transport;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.thingsboard.server.common.data.security.DeviceCredentials;

import java.util.UUID;

@Data
public class DeviceConfig {

    private UUID id;
    private String name;
    private DeviceCredentials credentials;

    public void setId(String id) {
        this.id = StringUtils.isNotEmpty(id) ? UUID.fromString(id) : null;
    }

}
