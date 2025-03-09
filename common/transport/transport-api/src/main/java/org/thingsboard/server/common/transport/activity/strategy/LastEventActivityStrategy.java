
package org.thingsboard.server.common.transport.activity.strategy;

public final class LastEventActivityStrategy implements ActivityStrategy {

    private static final LastEventActivityStrategy INSTANCE = new LastEventActivityStrategy();

    private LastEventActivityStrategy() {
    }

    public static LastEventActivityStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean onActivity() {
        return false;
    }

    @Override
    public boolean onReportingPeriodEnd() {
        return true;
    }

}
