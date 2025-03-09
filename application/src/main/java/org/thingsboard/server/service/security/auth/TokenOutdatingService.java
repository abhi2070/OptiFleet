
package org.thingsboard.server.service.security.auth;

import org.thingsboard.server.common.data.id.UserId;

public interface TokenOutdatingService {

    boolean isOutdated(String token, UserId userId);

}
