
package org.thingsboard.server.common.data.util;

import org.thingsboard.server.common.data.exception.ThingsboardException;

public interface ThrowingRunnable {

    void run() throws ThingsboardException;

    default ThrowingRunnable andThen(ThrowingRunnable after) {
        return () -> {
            this.run();
            after.run();
        };
    }

}
