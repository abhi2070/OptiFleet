
package org.thingsboard.server.service.notification.rule.trigger;

import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmApiCallResult;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.alarm.AlarmStatusFilter;
import org.thingsboard.server.common.data.notification.info.AlarmNotificationInfo;
import org.thingsboard.server.common.data.notification.info.RuleOriginatedNotificationInfo;
import org.thingsboard.server.common.data.notification.rule.trigger.AlarmTrigger;
import org.thingsboard.server.common.data.notification.rule.trigger.config.AlarmNotificationRuleTriggerConfig;
import org.thingsboard.server.common.data.notification.rule.trigger.config.AlarmNotificationRuleTriggerConfig.AlarmAction;
import org.thingsboard.server.common.data.notification.rule.trigger.config.AlarmNotificationRuleTriggerConfig.ClearRule;
import org.thingsboard.server.common.data.notification.rule.trigger.config.NotificationRuleTriggerType;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.thingsboard.server.common.data.util.CollectionsUtil.emptyOrContains;

@Service
public class AlarmTriggerProcessor implements NotificationRuleTriggerProcessor<AlarmTrigger, AlarmNotificationRuleTriggerConfig> {

    @Override
    public boolean matchesFilter(AlarmTrigger trigger, AlarmNotificationRuleTriggerConfig triggerConfig) {
        AlarmApiCallResult alarmUpdate = trigger.getAlarmUpdate();
        Alarm alarm = alarmUpdate.getAlarm();
        if (!typeMatches(alarm, triggerConfig)) {
            return false;
        }

        if (alarmUpdate.isCreated()) {
            if (triggerConfig.getNotifyOn().contains(AlarmAction.CREATED)) {
                return severityMatches(alarm, triggerConfig);
            }
        }  else if (alarmUpdate.isSeverityChanged()) {
            if (triggerConfig.getNotifyOn().contains(AlarmAction.SEVERITY_CHANGED)) {
                return severityMatches(alarmUpdate.getOld(), triggerConfig) || severityMatches(alarm, triggerConfig);
            }  else {
                // if we haven't yet sent notification about the alarm
                return !severityMatches(alarmUpdate.getOld(), triggerConfig) && severityMatches(alarm, triggerConfig);
            }
        } else if (alarmUpdate.isAcknowledged()) {
            if (triggerConfig.getNotifyOn().contains(AlarmAction.ACKNOWLEDGED)) {
                return severityMatches(alarm, triggerConfig);
            }
        } else if (alarmUpdate.isCleared()) {
            if (triggerConfig.getNotifyOn().contains(AlarmAction.CLEARED)) {
                return severityMatches(alarm, triggerConfig);
            }
        }
        return false;
    }

    @Override
    public boolean matchesClearRule(AlarmTrigger trigger, AlarmNotificationRuleTriggerConfig triggerConfig) {
        AlarmApiCallResult alarmUpdate = trigger.getAlarmUpdate();
        Alarm alarm = alarmUpdate.getAlarm();
        if (!typeMatches(alarm, triggerConfig)) {
            return false;
        }
        if (alarmUpdate.isDeleted()) {
            return true;
        }
        ClearRule clearRule = triggerConfig.getClearRule();
        if (clearRule != null) {
            if (isNotEmpty(clearRule.getAlarmStatuses())) {
                return AlarmStatusFilter.from(clearRule.getAlarmStatuses()).matches(alarm);
            }
        }
        return false;
    }

    private boolean severityMatches(Alarm alarm, AlarmNotificationRuleTriggerConfig triggerConfig) {
        return emptyOrContains(triggerConfig.getAlarmSeverities(), alarm.getSeverity());
    }

    private boolean typeMatches(Alarm alarm, AlarmNotificationRuleTriggerConfig triggerConfig) {
        return emptyOrContains(triggerConfig.getAlarmTypes(), alarm.getType());
    }

    @Override
    public RuleOriginatedNotificationInfo constructNotificationInfo(AlarmTrigger trigger) {
        AlarmApiCallResult alarmUpdate = trigger.getAlarmUpdate();
        AlarmInfo alarmInfo = alarmUpdate.getAlarm();
        return AlarmNotificationInfo.builder()
                .alarmId(alarmInfo.getUuidId())
                .alarmType(alarmInfo.getType())
                .action(alarmUpdate.isCreated() ? "created" :
                        alarmUpdate.isSeverityChanged() ? "severity changed" :
                        alarmUpdate.isAcknowledged() ? "acknowledged" :
                        alarmUpdate.isCleared() ? "cleared" :
                        alarmUpdate.isDeleted() ? "deleted" : null)
                .alarmOriginator(alarmInfo.getOriginator())
                .alarmOriginatorName(alarmInfo.getOriginatorName())
                .alarmSeverity(alarmInfo.getSeverity())
                .alarmStatus(alarmInfo.getStatus())
                .acknowledged(alarmInfo.isAcknowledged())
                .cleared(alarmInfo.isCleared())
                .alarmCustomerId(alarmInfo.getCustomerId())
                .dashboardId(alarmInfo.getDashboardId())
                .build();
    }

    @Override
    public NotificationRuleTriggerType getTriggerType() {
        return NotificationRuleTriggerType.ALARM;
    }

}
