
package org.thingsboard.server.common.transport.activity;

public interface ActivityReportCallback<Key> {

    void onSuccess(Key key, long reportedTime);

    void onFailure(Key key, Throwable t);

}
