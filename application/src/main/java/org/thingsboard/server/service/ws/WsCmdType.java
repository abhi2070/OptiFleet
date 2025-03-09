
package org.thingsboard.server.service.ws;

public enum WsCmdType {
    AUTH,

    ATTRIBUTES,
    TIMESERIES,
    TIMESERIES_HISTORY,
    ENTITY_DATA,
    ENTITY_COUNT,
    ALARM_DATA,
    ALARM_COUNT,

    NOTIFICATIONS,
    NOTIFICATIONS_COUNT,
    MARK_NOTIFICATIONS_AS_READ,
    MARK_ALL_NOTIFICATIONS_AS_READ,

    ALARM_DATA_UNSUBSCRIBE,
    ALARM_COUNT_UNSUBSCRIBE,
    ENTITY_DATA_UNSUBSCRIBE,
    ENTITY_COUNT_UNSUBSCRIBE,
    NOTIFICATIONS_UNSUBSCRIBE
}
