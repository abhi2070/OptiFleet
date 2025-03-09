
package org.thingsboard.server.common.msg.tools;

import lombok.Getter;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.exception.AbstractRateLimitException;

/**
 * Created by ashvayka on 22.10.18.
 */
public class TbRateLimitsException extends AbstractRateLimitException {
    @Getter
    private final EntityType entityType;

    public TbRateLimitsException(EntityType entityType) {
        super(entityType.name() + " rate limits reached!");
        this.entityType = entityType;
    }
}
