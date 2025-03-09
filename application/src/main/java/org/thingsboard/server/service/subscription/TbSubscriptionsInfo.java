
package org.thingsboard.server.service.subscription;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Information about the local websocket subscriptions.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"seqNumber"})
@ToString
public class TbSubscriptionsInfo {

    protected boolean notifications;
    protected boolean alarms;
    protected boolean tsAllKeys;
    protected Set<String> tsKeys;
    protected boolean attrAllKeys;
    protected Set<String> attrKeys;
    protected int seqNumber;

    public boolean isEmpty() {
        return !notifications && !alarms && !tsAllKeys && !attrAllKeys && tsKeys == null && attrKeys == null;
    }

    protected TbSubscriptionsInfo copy() {
        return copy(0);
    }

    protected TbSubscriptionsInfo copy(int seqNumber) {
        return new TbSubscriptionsInfo(notifications, alarms, tsAllKeys, tsKeys, attrAllKeys, attrKeys, seqNumber);
    }

}
