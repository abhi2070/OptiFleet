
package org.thingsboard.rule.engine.filter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.api.EmptyNodeConfiguration;
import org.thingsboard.rule.engine.api.RuleEngineDeviceProfileCache;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.common.msg.queue.TbMsgCallback;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TbDeviceTypeSwitchNodeTest {

    private DeviceId deviceId;
    private DeviceId deviceIdDeleted;
    private TbContext ctx;
    private TbDeviceTypeSwitchNode node;
    private TbMsgCallback callback;

    @BeforeEach
    void setUp() throws TbNodeException {
        TenantId tenantId = new TenantId(UUID.randomUUID());
        deviceId = new DeviceId(UUID.randomUUID());
        deviceIdDeleted = new DeviceId(UUID.randomUUID());

        DeviceProfile deviceProfile = new DeviceProfile();
        deviceProfile.setTenantId(tenantId);
        deviceProfile.setName("TestDeviceProfile");

        //node
        EmptyNodeConfiguration config = new EmptyNodeConfiguration();
        node = new TbDeviceTypeSwitchNode();
        node.init(ctx, new TbNodeConfiguration(JacksonUtil.valueToTree(config)));

        //init mock
        ctx = mock(TbContext.class);
        RuleEngineDeviceProfileCache deviceProfileCache = mock(RuleEngineDeviceProfileCache.class);
        callback = mock(TbMsgCallback.class);

        when(ctx.getTenantId()).thenReturn(tenantId);
        when(ctx.getDeviceProfileCache()).thenReturn(deviceProfileCache);

        doReturn(deviceProfile).when(deviceProfileCache).get(tenantId, deviceId);
        doReturn(null).when(deviceProfileCache).get(tenantId, deviceIdDeleted);
    }

    @AfterEach
    void tearDown() {
        node.destroy();
    }

    @Test
    void givenMsg_whenOnMsg_then_Fail() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());
        assertThatThrownBy(() -> {
            node.onMsg(ctx, getTbMsg(customerId));
        }).isInstanceOf(TbNodeException.class).hasMessageContaining("Unsupported originator type");
    }

    @Test
    void givenMsg_whenOnMsg_EntityIdDeleted_then_Fail() {
        assertThatThrownBy(() -> {
            node.onMsg(ctx, getTbMsg(deviceIdDeleted));
        }).isInstanceOf(TbNodeException.class).hasMessageContaining("Device profile for entity id");
    }

    @Test
    void givenMsg_whenOnMsg_then_Success() throws TbNodeException {
        TbMsg msg = getTbMsg(deviceId);
        node.onMsg(ctx, msg);

        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx, times(1)).tellNext(newMsgCaptor.capture(), eq("TestDeviceProfile"));
        verify(ctx, never()).tellFailure(any(), any());

        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(msg);
    }

    private TbMsg getTbMsg(EntityId entityId) {
        return TbMsg.newMsg(TbMsgType.POST_ATTRIBUTES_REQUEST, entityId, TbMsgMetaData.EMPTY, TbMsg.EMPTY_JSON_OBJECT, callback);
    }
}
