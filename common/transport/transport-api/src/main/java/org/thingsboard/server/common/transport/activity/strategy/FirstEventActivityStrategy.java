
package org.thingsboard.server.common.transport.activity.strategy;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class FirstEventActivityStrategy implements ActivityStrategy {

    private boolean firstEventReceived;

    @Override
    public synchronized boolean onActivity() {
        if (!firstEventReceived) {
            firstEventReceived = true;
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean onReportingPeriodEnd() {
        firstEventReceived = false;
        return false;
    }

}
