
package org.thingsboard.rule.engine.filter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityIdFactory;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TbOriginatorTypeSwitchNodeTest {

    private static final UUID RANDOM_UUID = UUID.randomUUID();

    private TbOriginatorTypeSwitchNode node;

    private TbContext ctx;

    @BeforeEach
    void setUp() {
        ctx = mock(TbContext.class);
        node = new TbOriginatorTypeSwitchNode();
    }

    @AfterEach
    void tearDown() {
        node.destroy();
    }

    @Test
    void givenAllTypes_whenOnMsg_then_allTypesSupported() throws TbNodeException {
        // GIVEN
        List<TbMsg> tbMsgList = new ArrayList<>();
        var entityTypes = EntityType.values();
        for (var entityType : entityTypes) {
            var entityId = EntityIdFactory.getByTypeAndUuid(entityType, RANDOM_UUID);
            tbMsgList.add(getTbMsg(entityId));
        }

        // WHEN
        for (TbMsg tbMsg : tbMsgList) {
            node.onMsg(ctx, tbMsg);
        }

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        ArgumentCaptor<String> nodeConnectionCapture = ArgumentCaptor.forClass(String.class);
        verify(ctx, times(tbMsgList.size())).tellNext(newMsgCaptor.capture(), nodeConnectionCapture.capture());
        verify(ctx, never()).tellFailure(any(), any());
        var resultMsgs = newMsgCaptor.getAllValues();
        var resultNodeConnections = nodeConnectionCapture.getAllValues();
        for (int i = 0; i < resultMsgs.size(); i++) {
            var msg = resultMsgs.get(i);
            assertThat(msg).isNotNull();
            assertThat(msg).isSameAs(tbMsgList.get(i));
            assertThat(resultNodeConnections.get(i))
                    .isEqualTo(msg.getOriginator().getEntityType().getNormalName());
        }
    }

    private TbMsg getTbMsg(EntityId entityId) {
        return TbMsg.newMsg(TbMsgType.POST_ATTRIBUTES_REQUEST, entityId, TbMsgMetaData.EMPTY, TbMsg.EMPTY_JSON_OBJECT);
    }

}
