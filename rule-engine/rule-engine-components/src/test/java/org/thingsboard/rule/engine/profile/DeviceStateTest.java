
package org.thingsboard.rule.engine.profile;

import com.google.common.util.concurrent.Futures;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.api.RuleEngineAlarmService;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.alarm.AlarmApiCallResult;
import org.thingsboard.server.common.data.alarm.AlarmCreateOrUpdateActiveRequest;
import org.thingsboard.server.common.data.alarm.AlarmInfo;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.device.profile.AlarmCondition;
import org.thingsboard.server.common.data.device.profile.AlarmConditionFilter;
import org.thingsboard.server.common.data.device.profile.AlarmConditionFilterKey;
import org.thingsboard.server.common.data.device.profile.AlarmConditionKeyType;
import org.thingsboard.server.common.data.device.profile.AlarmRule;
import org.thingsboard.server.common.data.device.profile.DeviceProfileAlarm;
import org.thingsboard.server.common.data.device.profile.DeviceProfileData;
import org.thingsboard.server.common.data.device.profile.SimpleAlarmConditionSpec;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.data.query.BooleanFilterPredicate;
import org.thingsboard.server.common.data.query.EntityKeyValueType;
import org.thingsboard.server.common.data.query.FilterPredicateValue;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.device.DeviceService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeviceStateTest {

    private TbContext ctx;

    @Before
    public void beforeEach() {
        ctx = mock(TbContext.class);

        when(ctx.getDeviceService()).thenReturn(mock(DeviceService.class));

        AttributesService attributesService = mock(AttributesService.class);
        when(attributesService.find(any(), any(), any(), anyCollection())).thenReturn(Futures.immediateFuture(Collections.emptyList()));
        when(ctx.getAttributesService()).thenReturn(attributesService);

        RuleEngineAlarmService alarmService = mock(RuleEngineAlarmService.class);
        when(alarmService.findLatestActiveByOriginatorAndType(any(), any(), any())).thenReturn(null);
        when(alarmService.createAlarm(any())).thenAnswer(invocationOnMock -> {
            AlarmCreateOrUpdateActiveRequest request = invocationOnMock.getArgument(0);
            return AlarmApiCallResult.builder()
                    .successful(true)
                    .created(true)
                    .modified(true)
                    .alarm(new AlarmInfo(new Alarm(new AlarmId(UUID.randomUUID()))))
                    .build();
        });
        when(ctx.getAlarmService()).thenReturn(alarmService);

        when(ctx.newMsg(any(), any(TbMsgType.class), any(), any(), any(), any())).thenAnswer(invocationOnMock -> {
            TbMsgType type = invocationOnMock.getArgument(1);
            String data = invocationOnMock.getArgument(invocationOnMock.getArguments().length - 1);
            return TbMsg.newMsg(type, null, TbMsgMetaData.EMPTY, data);
        });

    }

    @Test
    public void whenAttributeIsDeleted_thenUnneededAlarmRulesAreNotReevaluated() throws Exception {

        DeviceProfileAlarm alarmConfig = createAlarmConfigWithBoolAttrCondition("enabled", false);
        DeviceId deviceId = new DeviceId(UUID.randomUUID());
        DeviceState deviceState = createDeviceState(deviceId, alarmConfig);

        TbMsg attributeUpdateMsg = TbMsg.newMsg(TbMsgType.POST_ATTRIBUTES_REQUEST,
                deviceId, TbMsgMetaData.EMPTY, "{ \"enabled\": false }");

        deviceState.process(ctx, attributeUpdateMsg);

        ArgumentCaptor<TbMsg> resultMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx).enqueueForTellNext(resultMsgCaptor.capture(), eq("Alarm Created"));
        Alarm alarm = JacksonUtil.fromString(resultMsgCaptor.getValue().getData(), Alarm.class);

        deviceState.process(ctx, TbMsg.newMsg(TbMsgType.ALARM_CLEAR, deviceId, TbMsgMetaData.EMPTY, JacksonUtil.toString(alarm)));
        reset(ctx);

        String deletedAttributes = "{ \"attributes\": [ \"other\" ] }";
        deviceState.process(ctx, TbMsg.newMsg(TbMsgType.ATTRIBUTES_DELETED, deviceId, TbMsgMetaData.EMPTY, deletedAttributes));
        verify(ctx, never()).enqueueForTellNext(any(), anyString());
    }

    @Test
    public void whenDeletingClearedAlarm_thenNoError() throws Exception {
        DeviceProfileAlarm alarmConfig = createAlarmConfigWithBoolAttrCondition("enabled", false);
        DeviceId deviceId = new DeviceId(UUID.randomUUID());
        DeviceState deviceState = createDeviceState(deviceId, alarmConfig);

        TbMsg attributeUpdateMsg = TbMsg.newMsg(TbMsgType.POST_ATTRIBUTES_REQUEST,
                deviceId, TbMsgMetaData.EMPTY, "{ \"enabled\": false }");

        deviceState.process(ctx, attributeUpdateMsg);
        ArgumentCaptor<TbMsg> resultMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx).enqueueForTellNext(resultMsgCaptor.capture(), eq("Alarm Created"));
        Alarm alarm = JacksonUtil.fromString(resultMsgCaptor.getValue().getData(), Alarm.class);

        deviceState.process(ctx, TbMsg.newMsg(TbMsgType.ALARM_CLEAR, deviceId, TbMsgMetaData.EMPTY, JacksonUtil.toString(alarm)));

        TbMsg alarmDeleteNotification = TbMsg.newMsg(TbMsgType.ALARM_DELETE, deviceId, TbMsgMetaData.EMPTY, JacksonUtil.toString(alarm));
        assertDoesNotThrow(() -> {
            deviceState.process(ctx, alarmDeleteNotification);
        });
    }


    private DeviceState createDeviceState(DeviceId deviceId, DeviceProfileAlarm... alarmConfigs) {
        DeviceProfile deviceProfile = new DeviceProfile();
        DeviceProfileData profileData = new DeviceProfileData();
        profileData.setAlarms(List.of(alarmConfigs));
        deviceProfile.setProfileData(profileData);

        ProfileState profileState = new ProfileState(deviceProfile);
        return new DeviceState(ctx, new TbDeviceProfileNodeConfiguration(),
                deviceId, profileState, null);
    }

    private DeviceProfileAlarm createAlarmConfigWithBoolAttrCondition(String key, boolean value) {

        AlarmConditionFilter condition = new AlarmConditionFilter();
        condition.setKey(new AlarmConditionFilterKey(AlarmConditionKeyType.ATTRIBUTE, key));
        condition.setValueType(EntityKeyValueType.BOOLEAN);
        BooleanFilterPredicate predicate = new BooleanFilterPredicate();
        predicate.setOperation(BooleanFilterPredicate.BooleanOperation.EQUAL);
        predicate.setValue(new FilterPredicateValue<>(value));
        condition.setPredicate(predicate);

        DeviceProfileAlarm alarmConfig = new DeviceProfileAlarm();
        alarmConfig.setId("MyAlarmID");
        alarmConfig.setAlarmType("MyAlarm");
        AlarmRule alarmRule = new AlarmRule();
        AlarmCondition alarmCondition = new AlarmCondition();
        alarmCondition.setSpec(new SimpleAlarmConditionSpec());
        alarmCondition.setCondition(List.of(condition));
        alarmRule.setCondition(alarmCondition);
        alarmConfig.setCreateRules(new TreeMap<>(Map.of(AlarmSeverity.CRITICAL, alarmRule)));

        return alarmConfig;
    }

}
