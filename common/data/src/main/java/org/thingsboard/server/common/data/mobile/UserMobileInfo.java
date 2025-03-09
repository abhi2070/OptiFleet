
package org.thingsboard.server.common.data.mobile;

import lombok.Data;

import java.util.Map;

@Data
public class UserMobileInfo {

    private Map<String, MobileSessionInfo> sessions;

}
