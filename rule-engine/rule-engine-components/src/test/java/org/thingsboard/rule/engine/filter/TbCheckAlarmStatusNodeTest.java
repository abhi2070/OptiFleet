
package org.thingsboard.rule.engine.filter;

import com.google.common.util.concurrent.Futures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.TestDbCallbackExecutor;
import org.thingsboard.rule.engine.api.RuleEngineAlarmService;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.alarm.Alarm;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.data.msg.TbNodeConnectionType;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TbCheckAlarmStatusNodeTest {

    private static final TenantId TENANT_ID = new TenantId(UUID.randomUUID());
    private static final DeviceId DEVICE_ID = new DeviceId(UUID.randomUUID());
    private static final AlarmId ALARM_ID = new AlarmId(UUID.randomUUID());
    private static final TestDbCallbackExecutor DB_EXECUTOR = new TestDbCallbackExecutor();

    private TbCheckAlarmStatusNode node;

    private TbContext ctx;
    private RuleEngineAlarmService alarmService;

    @BeforeEach
    void setUp() throws TbNodeException {
        var config = new TbCheckAlarmStatusNodeConfig().defaultConfiguration();

        ctx = mock(TbContext.class);
        alarmService = mock(RuleEngineAlarmService.class);

        when(ctx.getTenantId()).thenReturn(TENANT_ID);
        when(ctx.getAlarmService()).thenReturn(alarmService);
        when(ctx.getDbCallbackExecutor()).thenReturn(DB_EXECUTOR);

        node = new TbCheckAlarmStatusNode();
        node.init(ctx, new TbNodeConfiguration(JacksonUtil.valueToTree(config)));
    }

    @AfterEach
    void tearDown() {
        node.destroy();
    }

    @Test
    void givenActiveAlarm_whenOnMsg_then_True() throws TbNodeException {
        // GIVEN
        var alarm = new Alarm();
        alarm.setId(ALARM_ID);
        alarm.setOriginator(DEVICE_ID);
        alarm.setType("General Alarm");

        String msgData = JacksonUtil.toString(alarm);
        TbMsg msg = getTbMsg(msgData);

        when(alarmService.findAlarmByIdAsync(TENANT_ID, ALARM_ID)).thenReturn(Futures.immediateFuture(alarm));

        // WHEN
        node.onMsg(ctx, msg);

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx, times(1)).tellNext(newMsgCaptor.capture(), eq(TbNodeConnectionType.TRUE));
        verify(ctx, never()).tellFailure(any(), any());
        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(msg);
    }

    @Test
    void givenClearedAlarm_whenOnMsg_then_False() throws TbNodeException {
        // GIVEN
        var alarm = new Alarm();
        alarm.setId(ALARM_ID);
        alarm.setOriginator(DEVICE_ID);
        alarm.setType("General Alarm");
        alarm.setCleared(true);

        String msgData = JacksonUtil.toString(alarm);
        TbMsg msg = getTbMsg(msgData);

        when(alarmService.findAlarmByIdAsync(TENANT_ID, ALARM_ID)).thenReturn(Futures.immediateFuture(alarm));

        // WHEN
        node.onMsg(ctx, msg);

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx, times(1)).tellNext(newMsgCaptor.capture(), eq(TbNodeConnectionType.FALSE));
        verify(ctx, never()).tellFailure(any(), any());
        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(msg);
    }

    @Test
    void givenDeletedAlarm_whenOnMsg_then_Failure() throws TbNodeException {
        // GIVEN
        var alarm = new Alarm();
        alarm.setId(ALARM_ID);
        alarm.setOriginator(DEVICE_ID);
        alarm.setType("General Alarm");
        alarm.setCleared(true);

        String msgData = JacksonUtil.toString(alarm);
        TbMsg msg = getTbMsg(msgData);

        when(alarmService.findAlarmByIdAsync(TENANT_ID, ALARM_ID)).thenReturn(Futures.immediateFuture(null));

        // WHEN
        node.onMsg(ctx, msg);

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(ctx, times(1)).tellFailure(newMsgCaptor.capture(), throwableCaptor.capture());
        verify(ctx, never()).tellSuccess(any());
        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(msg);
        Throwable value = throwableCaptor.getValue();
        assertThat(value).isInstanceOf(TbNodeException.class).hasMessage("No such alarm found.");
    }

    private TbMsg getTbMsg(String msgData) {
        return TbMsg.newMsg(TbMsgType.POST_ATTRIBUTES_REQUEST, DEVICE_ID, TbMsgMetaData.EMPTY, msgData);
    }

}
