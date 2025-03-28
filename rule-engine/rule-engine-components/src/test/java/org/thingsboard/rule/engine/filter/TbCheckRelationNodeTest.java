
package org.thingsboard.rule.engine.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.Futures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.TestDbCallbackExecutor;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.data.msg.TbNodeConnectionType;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.common.data.util.TbPair;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.dao.relation.RelationService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TbCheckRelationNodeTest {

    private static final TenantId TENANT_ID = new TenantId(UUID.randomUUID());
    private static final DeviceId ORIGINATOR_ID = new DeviceId(UUID.randomUUID());
    private static final TestDbCallbackExecutor DB_EXECUTOR = new TestDbCallbackExecutor();
    private static final TbMsg EMPTY_POST_ATTRIBUTES_MSG = TbMsg.newMsg(TbMsgType.POST_ATTRIBUTES_REQUEST, ORIGINATOR_ID, TbMsgMetaData.EMPTY, TbMsg.EMPTY_JSON_OBJECT);

    private TbCheckRelationNode node;

    private TbContext ctx;
    private RelationService relationService;

    @BeforeEach
    void setUp() {
        ctx = mock(TbContext.class);
        relationService = mock(RelationService.class);

        when(ctx.getTenantId()).thenReturn(TENANT_ID);
        when(ctx.getRelationService()).thenReturn(relationService);
        when(ctx.getDbCallbackExecutor()).thenReturn(DB_EXECUTOR);

        node = new TbCheckRelationNode();
    }

    @AfterEach
    void tearDown() {
        node.destroy();
    }

    @Test
    void givenDefaultConfig_whenInit_then_throwException() {
        // GIVEN
        var config = new TbCheckRelationNodeConfiguration().defaultConfiguration();

        // WHEN
        var exception = assertThrows(TbNodeException.class, () -> node.init(ctx, new TbNodeConfiguration(JacksonUtil.valueToTree(config))));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Entity should be specified!");
    }

    @Test
    void givenCustomConfigWithCheckRelationToSpecificEntity_whenOnMsg_then_True() throws TbNodeException {
        // GIVEN
        var config = new TbCheckRelationNodeConfiguration().defaultConfiguration();

        AssetId assetId = new AssetId(UUID.randomUUID());
        config.setEntityType(assetId.getEntityType().name());
        config.setEntityId(assetId.getId().toString());

        when(relationService.checkRelationAsync(TENANT_ID, ORIGINATOR_ID, assetId, config.getRelationType(), RelationTypeGroup.COMMON)).thenReturn(Futures.immediateFuture(true));
        node.init(ctx, new TbNodeConfiguration(JacksonUtil.valueToTree(config)));

        // WHEN
        node.onMsg(ctx, EMPTY_POST_ATTRIBUTES_MSG);

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx, times(1)).tellNext(newMsgCaptor.capture(), eq(TbNodeConnectionType.TRUE));
        verify(ctx, never()).tellFailure(any(), any());
        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(EMPTY_POST_ATTRIBUTES_MSG);
    }

    @Test
    void givenCustomConfigWithCheckRelationToSpecificEntity_whenOnMsg_then_False() throws TbNodeException {
        // GIVEN
        var config = new TbCheckRelationNodeConfiguration().defaultConfiguration();

        AssetId assetId = new AssetId(UUID.randomUUID());
        config.setEntityType(assetId.getEntityType().name());
        config.setEntityId(assetId.getId().toString());

        when(relationService.checkRelationAsync(TENANT_ID, ORIGINATOR_ID, assetId, config.getRelationType(), RelationTypeGroup.COMMON)).thenReturn(Futures.immediateFuture(false));
        node.init(ctx, new TbNodeConfiguration(JacksonUtil.valueToTree(config)));

        // WHEN
        node.onMsg(ctx, EMPTY_POST_ATTRIBUTES_MSG);

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx, times(1)).tellNext(newMsgCaptor.capture(), eq(TbNodeConnectionType.FALSE));
        verify(ctx, never()).tellFailure(any(), any());
        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(EMPTY_POST_ATTRIBUTES_MSG);
    }

    @Test
    void givenCustomConfigWithCheckRelationToSpecificEntityAndDirectionTo_whenOnMsg_then_True() throws TbNodeException {
        // GIVEN
        var config = new TbCheckRelationNodeConfiguration().defaultConfiguration();

        AssetId assetId = new AssetId(UUID.randomUUID());
        config.setEntityType(assetId.getEntityType().name());
        config.setEntityId(assetId.getId().toString());
        config.setDirection(EntitySearchDirection.TO.name());

        when(relationService.checkRelationAsync(TENANT_ID, assetId, ORIGINATOR_ID, config.getRelationType(), RelationTypeGroup.COMMON)).thenReturn(Futures.immediateFuture(true));
        node.init(ctx, new TbNodeConfiguration(JacksonUtil.valueToTree(config)));

        // WHEN
        node.onMsg(ctx, EMPTY_POST_ATTRIBUTES_MSG);

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx, times(1)).tellNext(newMsgCaptor.capture(), eq(TbNodeConnectionType.TRUE));
        verify(ctx, never()).tellFailure(any(), any());
        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(EMPTY_POST_ATTRIBUTES_MSG);
    }

    @Test
    void givenCustomConfigWithCheckRelationToSpecificEntityAndDirectionTo_whenOnMsg_then_False() throws TbNodeException {
        // GIVEN
        var config = new TbCheckRelationNodeConfiguration().defaultConfiguration();

        AssetId assetId = new AssetId(UUID.randomUUID());
        config.setEntityType(assetId.getEntityType().name());
        config.setEntityId(assetId.getId().toString());
        config.setDirection(EntitySearchDirection.TO.name());

        when(relationService.checkRelationAsync(TENANT_ID, assetId, ORIGINATOR_ID, config.getRelationType(), RelationTypeGroup.COMMON)).thenReturn(Futures.immediateFuture(false));
        node.init(ctx, new TbNodeConfiguration(JacksonUtil.valueToTree(config)));

        // WHEN
        node.onMsg(ctx, EMPTY_POST_ATTRIBUTES_MSG);

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx, times(1)).tellNext(newMsgCaptor.capture(), eq(TbNodeConnectionType.FALSE));
        verify(ctx, never()).tellFailure(any(), any());
        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(EMPTY_POST_ATTRIBUTES_MSG);
    }

    @Test
    void givenCustomConfig_whenOnMsg_then_True() throws TbNodeException {
        // GIVEN
        var config = new TbCheckRelationNodeConfiguration().defaultConfiguration();
        config.setCheckForSingleEntity(false);
        var entityRelation = new EntityRelation();
        entityRelation.setFrom(ORIGINATOR_ID);
        entityRelation.setTo(new AssetId(UUID.randomUUID()));
        entityRelation.setType(EntityRelation.CONTAINS_TYPE);
        entityRelation.setTypeGroup(RelationTypeGroup.COMMON);

        when(relationService.findByFromAndTypeAsync(TENANT_ID, ORIGINATOR_ID, config.getRelationType(), RelationTypeGroup.COMMON)).thenReturn(Futures.immediateFuture(List.of(entityRelation)));
        node.init(ctx, new TbNodeConfiguration(JacksonUtil.valueToTree(config)));

        // WHEN
        node.onMsg(ctx, EMPTY_POST_ATTRIBUTES_MSG);

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx, times(1)).tellNext(newMsgCaptor.capture(), eq(TbNodeConnectionType.TRUE));
        verify(ctx, never()).tellFailure(any(), any());
        verify(relationService, never()).findByToAndTypeAsync(any(), any(), anyString(), any());
        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(EMPTY_POST_ATTRIBUTES_MSG);
    }

    @Test
    void givenCustomConfig_whenOnMsg_then_False() throws TbNodeException {
        // GIVEN
        var config = new TbCheckRelationNodeConfiguration().defaultConfiguration();
        config.setCheckForSingleEntity(false);

        when(relationService.findByFromAndTypeAsync(TENANT_ID, ORIGINATOR_ID, config.getRelationType(), RelationTypeGroup.COMMON)).thenReturn(Futures.immediateFuture(Collections.emptyList()));
        node.init(ctx, new TbNodeConfiguration(JacksonUtil.valueToTree(config)));

        // WHEN
        node.onMsg(ctx, EMPTY_POST_ATTRIBUTES_MSG);

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx, times(1)).tellNext(newMsgCaptor.capture(), eq(TbNodeConnectionType.FALSE));
        verify(ctx, never()).tellFailure(any(), any());
        verify(relationService, never()).findByToAndTypeAsync(any(), any(), anyString(), any());
        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(EMPTY_POST_ATTRIBUTES_MSG);
    }

    @Test
    void givenCustomConfigDirectionTo_whenOnMsg_then_True() throws TbNodeException {
        // GIVEN
        var config = new TbCheckRelationNodeConfiguration().defaultConfiguration();
        config.setCheckForSingleEntity(false);
        config.setDirection(EntitySearchDirection.TO.name());
        var entityRelation = new EntityRelation();
        entityRelation.setFrom(new AssetId(UUID.randomUUID()));
        entityRelation.setTo(ORIGINATOR_ID);
        entityRelation.setType(EntityRelation.CONTAINS_TYPE);
        entityRelation.setTypeGroup(RelationTypeGroup.COMMON);

        when(relationService.findByToAndTypeAsync(TENANT_ID, ORIGINATOR_ID, config.getRelationType(), RelationTypeGroup.COMMON)).thenReturn(Futures.immediateFuture(List.of(entityRelation)));
        node.init(ctx, new TbNodeConfiguration(JacksonUtil.valueToTree(config)));

        // WHEN
        node.onMsg(ctx, EMPTY_POST_ATTRIBUTES_MSG);

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx, times(1)).tellNext(newMsgCaptor.capture(), eq(TbNodeConnectionType.TRUE));
        verify(ctx, never()).tellFailure(any(), any());
        verify(relationService, never()).findByFromAndTypeAsync(any(), any(), anyString(), any());
        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(EMPTY_POST_ATTRIBUTES_MSG);
    }

    @Test
    void givenCustomConfigDirectionTo_whenOnMsg_then_False() throws TbNodeException {
        // GIVEN
        var config = new TbCheckRelationNodeConfiguration().defaultConfiguration();
        config.setCheckForSingleEntity(false);
        config.setDirection(EntitySearchDirection.TO.name());

        when(relationService.findByToAndTypeAsync(TENANT_ID, ORIGINATOR_ID, config.getRelationType(), RelationTypeGroup.COMMON)).thenReturn(Futures.immediateFuture(Collections.emptyList()));
        node.init(ctx, new TbNodeConfiguration(JacksonUtil.valueToTree(config)));

        // WHEN
        node.onMsg(ctx, EMPTY_POST_ATTRIBUTES_MSG);

        // THEN
        ArgumentCaptor<TbMsg> newMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx, times(1)).tellNext(newMsgCaptor.capture(), eq(TbNodeConnectionType.FALSE));
        verify(ctx, never()).tellFailure(any(), any());
        verify(relationService, never()).findByFromAndTypeAsync(any(), any(), anyString(), any());
        TbMsg newMsg = newMsgCaptor.getValue();
        assertThat(newMsg).isNotNull();
        assertThat(newMsg).isSameAs(EMPTY_POST_ATTRIBUTES_MSG);
    }

    @Test
    void givenOldConfig_whenUpgrade_thenShouldReturnTrueResultWithNewConfig() throws Exception {
        // GIVEN
        var config = new TbCheckRelationNodeConfiguration().defaultConfiguration();
        config.setEntityType(ORIGINATOR_ID.getEntityType().name());
        config.setEntityId(ORIGINATOR_ID.getId().toString());
        String oldConfig = "{\"checkForSingleEntity\":true,\"direction\":\"TO\",\"entityType\":\"" + config.getEntityType() + "\",\"entityId\":\"" + config.getEntityId() + "\",\"relationType\":\"Contains\"}";
        JsonNode configJson = JacksonUtil.toJsonNode(oldConfig);
        // WHEN
        TbPair<Boolean, JsonNode> upgrade = node.upgrade(0, configJson);
        // THEN
        assertTrue(upgrade.getFirst());
        assertEquals(config, JacksonUtil.treeToValue(upgrade.getSecond(), config.getClass()));
    }

}
