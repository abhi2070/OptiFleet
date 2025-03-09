
package org.thingsboard.server.service.system;

import org.thingsboard.server.common.data.FeaturesInfo;
import org.thingsboard.server.common.data.SystemInfo;

public interface SystemInfoService {
    SystemInfo getSystemInfo();

    FeaturesInfo getFeaturesInfo();
}
