
package org.thingsboard.server.service.queue.ruleengine;

import java.io.Serializable;

public enum QueueEvent implements Serializable {

    PARTITION_CHANGE, CONFIG_UPDATE, DELETE

}
