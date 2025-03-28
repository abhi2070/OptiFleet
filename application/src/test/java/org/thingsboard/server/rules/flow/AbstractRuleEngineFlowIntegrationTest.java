
package org.thingsboard.server.rules.flow;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.flow.TbRuleChainInputNodeConfiguration;
import org.thingsboard.rule.engine.util.TbMsgSource;
import org.thingsboard.rule.engine.metadata.TbGetAttributesNode;
import org.thingsboard.rule.engine.metadata.TbGetAttributesNodeConfiguration;
import org.thingsboard.server.actors.ActorSystemContext;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.EventInfo;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.event.Event;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.StringDataEntry;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.data.msg.TbNodeConnectionType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.rule.NodeConnectionInfo;
import org.thingsboard.server.common.data.rule.RuleChain;
import org.thingsboard.server.common.data.rule.RuleChainMetaData;
import org.thingsboard.server.common.data.rule.RuleNode;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.common.msg.queue.QueueToRuleEngineMsg;
import org.thingsboard.server.common.msg.queue.TbMsgCallback;
import org.thingsboard.server.controller.AbstractRuleEngineControllerTest;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.event.EventService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Valerii Sosliuk
 */
@Slf4j
public abstract class AbstractRuleEngineFlowIntegrationTest extends AbstractRuleEngineControllerTest {

    protected Tenant savedTenant;
    protected User tenantAdmin;

    @Autowired
    protected ActorSystemContext actorSystem;

    @Autowired
    protected AttributesService attributesService;

    @Autowired
    protected EventService eventService;

    @Before
    public void beforeTest() throws Exception {

        EventService spyEventService = spy(eventService);

        Mockito.doAnswer((Answer<ListenableFuture<Void>>) invocation -> {
            Object[] args = invocation.getArguments();
            Event event = (Event) args[0];
            ListenableFuture<Void> future = eventService.saveAsync(event);
            try {
                future.get();
            } catch (Exception e) {}
            return future;
        }).when(spyEventService).saveAsync(Mockito.any(Event.class));

        ReflectionTestUtils.setField(actorSystem, "eventService", spyEventService);

        loginSysAdmin();

        Tenant tenant = new Tenant();
        tenant.setTitle("My tenant");
        savedTenant = doPost("/api/tenant", tenant, Tenant.class);
        Assert.assertNotNull(savedTenant);
        ruleChainService.deleteRuleChainsByTenantId(savedTenant.getId());

        tenantAdmin = new User();
        tenantAdmin.setAuthority(Authority.TENANT_ADMIN);
        tenantAdmin.setTenantId(savedTenant.getId());
        tenantAdmin.setEmail("tenant2@thingsboard.org");
        tenantAdmin.setFirstName("Joe");
        tenantAdmin.setLastName("Downs");

        createUserAndLogin(tenantAdmin, "testPassword1");
    }

    @After
    public void afterTest() throws Exception {
        loginSysAdmin();
        if (savedTenant != null) {
            doDelete("/api/tenant/" + savedTenant.getId().getId().toString()).andExpect(status().isOk());
        }
    }

    @Test
    public void testRuleChainWithTwoRules() throws Exception {
        // Creating Rule Chain
        RuleChain ruleChain = new RuleChain();
        ruleChain.setName("Simple Rule Chain");
        ruleChain.setTenantId(savedTenant.getId());
        ruleChain.setRoot(true);
        ruleChain.setDebugMode(true);
        ruleChain = saveRuleChain(ruleChain);
        Assert.assertNull(ruleChain.getFirstRuleNodeId());

        RuleChainMetaData metaData = new RuleChainMetaData();
        metaData.setRuleChainId(ruleChain.getId());

        RuleNode ruleNode1 = new RuleNode();
        ruleNode1.setName("Simple Rule Node 1");
        ruleNode1.setType(org.thingsboard.rule.engine.metadata.TbGetAttributesNode.class.getName());
        ruleNode1.setConfigurationVersion(TbGetAttributesNode.class.getAnnotation(org.thingsboard.rule.engine.api.RuleNode.class).version());
        ruleNode1.setDebugMode(true);
        TbGetAttributesNodeConfiguration configuration1 = new TbGetAttributesNodeConfiguration();
        configuration1.setFetchTo(TbMsgSource.METADATA);
        configuration1.setServerAttributeNames(Collections.singletonList("serverAttributeKey1"));
        ruleNode1.setConfiguration(JacksonUtil.valueToTree(configuration1));

        RuleNode ruleNode2 = new RuleNode();
        ruleNode2.setName("Simple Rule Node 2");
        ruleNode2.setType(org.thingsboard.rule.engine.metadata.TbGetAttributesNode.class.getName());
        ruleNode2.setConfigurationVersion(TbGetAttributesNode.class.getAnnotation(org.thingsboard.rule.engine.api.RuleNode.class).version());
        ruleNode2.setDebugMode(true);
        TbGetAttributesNodeConfiguration configuration2 = new TbGetAttributesNodeConfiguration();
        configuration2.setFetchTo(TbMsgSource.METADATA);
        configuration2.setServerAttributeNames(Collections.singletonList("serverAttributeKey2"));
        ruleNode2.setConfiguration(JacksonUtil.valueToTree(configuration2));


        metaData.setNodes(Arrays.asList(ruleNode1, ruleNode2));
        metaData.setFirstNodeIndex(0);
        metaData.addConnectionInfo(0, 1, TbNodeConnectionType.SUCCESS);
        metaData = saveRuleChainMetaData(metaData);
        Assert.assertNotNull(metaData);

        ruleChain = getRuleChain(ruleChain.getId());
        Assert.assertNotNull(ruleChain.getFirstRuleNodeId());

        // Saving the device
        Device device = new Device();
        device.setName("My device");
        device.setType("default");
        device = doPost("/api/device", device, Device.class);

        attributesService.save(device.getTenantId(), device.getId(), DataConstants.SERVER_SCOPE,
                Collections.singletonList(new BaseAttributeKvEntry(new StringDataEntry("serverAttributeKey1", "serverAttributeValue1"), System.currentTimeMillis()))).get();
        attributesService.save(device.getTenantId(), device.getId(), DataConstants.SERVER_SCOPE,
                Collections.singletonList(new BaseAttributeKvEntry(new StringDataEntry("serverAttributeKey2", "serverAttributeValue2"), System.currentTimeMillis()))).get();

        TbMsgCallback tbMsgCallback = Mockito.mock(TbMsgCallback.class);
        Mockito.when(tbMsgCallback.isMsgValid()).thenReturn(true);
        TbMsg tbMsg = TbMsg.newMsg(TbMsgType.POST_TELEMETRY_REQUEST, device.getId(), TbMsgMetaData.EMPTY, TbMsg.EMPTY_JSON_OBJECT, tbMsgCallback);
        QueueToRuleEngineMsg qMsg = new QueueToRuleEngineMsg(savedTenant.getId(), tbMsg, null, null);
        // Pushing Message to the system
        actorSystem.tell(qMsg);
        Mockito.verify(tbMsgCallback, Mockito.timeout(10000)).onSuccess();

        PageData<EventInfo> eventsPage = getDebugEvents(savedTenant.getId(), ruleChain.getFirstRuleNodeId(), 1000);
        List<EventInfo> events = eventsPage.getData().stream().filter(filterByPostTelemetryEventType()).collect(Collectors.toList());
        Assert.assertEquals(2, events.size());

        EventInfo inEvent = events.stream().filter(e -> e.getBody().get("type").asText().equals(DataConstants.IN)).findFirst().get();
        Assert.assertEquals(ruleChain.getFirstRuleNodeId(), inEvent.getEntityId());
        Assert.assertEquals(device.getId().getId().toString(), inEvent.getBody().get("entityId").asText());

        EventInfo outEvent = events.stream().filter(e -> e.getBody().get("type").asText().equals(DataConstants.OUT)).findFirst().get();
        Assert.assertEquals(ruleChain.getFirstRuleNodeId(), outEvent.getEntityId());
        Assert.assertEquals(device.getId().getId().toString(), outEvent.getBody().get("entityId").asText());

        Assert.assertEquals("serverAttributeValue1", getMetadata(outEvent).get("ss_serverAttributeKey1").asText());

        RuleChain finalRuleChain = ruleChain;
        RuleNode lastRuleNode = metaData.getNodes().stream().filter(node -> !node.getId().equals(finalRuleChain.getFirstRuleNodeId())).findFirst().get();

        eventsPage = getDebugEvents(savedTenant.getId(), lastRuleNode.getId(), 1000);
        events = eventsPage.getData().stream().filter(filterByPostTelemetryEventType()).collect(Collectors.toList());

        Assert.assertEquals(2, events.size());

        inEvent = events.stream().filter(e -> e.getBody().get("type").asText().equals(DataConstants.IN)).findFirst().get();
        Assert.assertEquals(lastRuleNode.getId(), inEvent.getEntityId());
        Assert.assertEquals(device.getId().getId().toString(), inEvent.getBody().get("entityId").asText());

        outEvent = events.stream().filter(e -> e.getBody().get("type").asText().equals(DataConstants.OUT)).findFirst().get();
        Assert.assertEquals(lastRuleNode.getId(), outEvent.getEntityId());
        Assert.assertEquals(device.getId().getId().toString(), outEvent.getBody().get("entityId").asText());

        Assert.assertEquals("serverAttributeValue1", getMetadata(outEvent).get("ss_serverAttributeKey1").asText());
        Assert.assertEquals("serverAttributeValue2", getMetadata(outEvent).get("ss_serverAttributeKey2").asText());
    }

    @Test
    public void testTwoRuleChainsWithTwoRules() throws Exception {
        // Creating Rule Chain
        RuleChain rootRuleChain = new RuleChain();
        rootRuleChain.setName("Root Rule Chain");
        rootRuleChain.setTenantId(savedTenant.getId());
        rootRuleChain.setRoot(true);
        rootRuleChain.setDebugMode(true);
        rootRuleChain = saveRuleChain(rootRuleChain);
        Assert.assertNull(rootRuleChain.getFirstRuleNodeId());

        // Creating Rule Chain
        RuleChain secondaryRuleChain = new RuleChain();
        secondaryRuleChain.setName("Secondary Rule Chain");
        secondaryRuleChain.setTenantId(savedTenant.getId());
        secondaryRuleChain.setRoot(false);
        secondaryRuleChain.setDebugMode(true);
        secondaryRuleChain = saveRuleChain(secondaryRuleChain);
        Assert.assertNull(secondaryRuleChain.getFirstRuleNodeId());

        RuleChainMetaData rootMetaData = new RuleChainMetaData();
        rootMetaData.setRuleChainId(rootRuleChain.getId());

        RuleNode ruleNode1 = new RuleNode();
        ruleNode1.setName("Simple Rule Node 1");
        ruleNode1.setType(org.thingsboard.rule.engine.metadata.TbGetAttributesNode.class.getName());
        ruleNode1.setConfigurationVersion(TbGetAttributesNode.class.getAnnotation(org.thingsboard.rule.engine.api.RuleNode.class).version());
        ruleNode1.setDebugMode(true);
        TbGetAttributesNodeConfiguration configuration1 = new TbGetAttributesNodeConfiguration();
        configuration1.setFetchTo(TbMsgSource.METADATA);
        configuration1.setServerAttributeNames(Collections.singletonList("serverAttributeKey1"));
        ruleNode1.setConfiguration(JacksonUtil.valueToTree(configuration1));

        RuleNode ruleNode12 = new RuleNode();
        ruleNode12.setName("Simple Rule Node 1");
        ruleNode12.setType(org.thingsboard.rule.engine.flow.TbRuleChainInputNode.class.getName());
        ruleNode12.setDebugMode(true);
        TbRuleChainInputNodeConfiguration configuration12 = new TbRuleChainInputNodeConfiguration();
        configuration12.setRuleChainId(secondaryRuleChain.getId().getId().toString());
        ruleNode12.setConfiguration(JacksonUtil.valueToTree(configuration12));

        rootMetaData.setNodes(Arrays.asList(ruleNode1, ruleNode12));
        rootMetaData.setFirstNodeIndex(0);
        NodeConnectionInfo connection = new NodeConnectionInfo();
        connection.setFromIndex(0);
        connection.setToIndex(1);
        connection.setType(TbNodeConnectionType.SUCCESS);
        rootMetaData.setConnections(Collections.singletonList(connection));
        rootMetaData = saveRuleChainMetaData(rootMetaData);
        Assert.assertNotNull(rootMetaData);

        rootRuleChain = getRuleChain(rootRuleChain.getId());
        Assert.assertNotNull(rootRuleChain.getFirstRuleNodeId());

        RuleChainMetaData secondaryMetaData = new RuleChainMetaData();
        secondaryMetaData.setRuleChainId(secondaryRuleChain.getId());

        RuleNode ruleNode2 = new RuleNode();
        ruleNode2.setName("Simple Rule Node 2");
        ruleNode2.setType(org.thingsboard.rule.engine.metadata.TbGetAttributesNode.class.getName());
        ruleNode2.setConfigurationVersion(TbGetAttributesNode.class.getAnnotation(org.thingsboard.rule.engine.api.RuleNode.class).version());
        ruleNode2.setDebugMode(true);
        TbGetAttributesNodeConfiguration configuration2 = new TbGetAttributesNodeConfiguration();
        configuration2.setFetchTo(TbMsgSource.METADATA);
        configuration2.setServerAttributeNames(Collections.singletonList("serverAttributeKey2"));
        ruleNode2.setConfiguration(JacksonUtil.valueToTree(configuration2));

        secondaryMetaData.setNodes(Collections.singletonList(ruleNode2));
        secondaryMetaData.setFirstNodeIndex(0);
        secondaryMetaData = saveRuleChainMetaData(secondaryMetaData);
        Assert.assertNotNull(secondaryMetaData);

        // Saving the device
        Device device = new Device();
        device.setName("My device");
        device.setType("default");
        device = doPost("/api/device", device, Device.class);

        attributesService.save(device.getTenantId(), device.getId(), DataConstants.SERVER_SCOPE,
                Collections.singletonList(new BaseAttributeKvEntry(new StringDataEntry("serverAttributeKey1", "serverAttributeValue1"), System.currentTimeMillis()))).get();
        attributesService.save(device.getTenantId(), device.getId(), DataConstants.SERVER_SCOPE,
                Collections.singletonList(new BaseAttributeKvEntry(new StringDataEntry("serverAttributeKey2", "serverAttributeValue2"), System.currentTimeMillis()))).get();

        TbMsgCallback tbMsgCallback = Mockito.mock(TbMsgCallback.class);
        Mockito.when(tbMsgCallback.isMsgValid()).thenReturn(true);
        TbMsg tbMsg = TbMsg.newMsg(TbMsgType.POST_TELEMETRY_REQUEST, device.getId(), TbMsgMetaData.EMPTY, TbMsg.EMPTY_JSON_OBJECT, tbMsgCallback);
        QueueToRuleEngineMsg qMsg = new QueueToRuleEngineMsg(savedTenant.getId(), tbMsg, null, null);
        // Pushing Message to the system
        actorSystem.tell(qMsg);

        Mockito.verify(tbMsgCallback, Mockito.timeout(10000)).onSuccess();

        PageData<EventInfo> eventsPage = getDebugEvents(savedTenant.getId(), rootRuleChain.getFirstRuleNodeId(), 1000);
        List<EventInfo> events = eventsPage.getData().stream().filter(filterByPostTelemetryEventType()).collect(Collectors.toList());

        Assert.assertEquals(2, events.size());

        EventInfo inEvent = events.stream().filter(e -> e.getBody().get("type").asText().equals(DataConstants.IN)).findFirst().get();
        Assert.assertEquals(rootRuleChain.getFirstRuleNodeId(), inEvent.getEntityId());
        Assert.assertEquals(device.getId().getId().toString(), inEvent.getBody().get("entityId").asText());

        EventInfo outEvent = events.stream().filter(e -> e.getBody().get("type").asText().equals(DataConstants.OUT)).findFirst().get();
        Assert.assertEquals(rootRuleChain.getFirstRuleNodeId(), outEvent.getEntityId());
        Assert.assertEquals(device.getId().getId().toString(), outEvent.getBody().get("entityId").asText());

        Assert.assertEquals("serverAttributeValue1", getMetadata(outEvent).get("ss_serverAttributeKey1").asText());

        RuleChain finalRuleChain = rootRuleChain;
        RuleNode lastRuleNode = secondaryMetaData.getNodes().stream().filter(node -> !node.getId().equals(finalRuleChain.getFirstRuleNodeId())).findFirst().get();

        eventsPage = getDebugEvents(savedTenant.getId(), lastRuleNode.getId(), 1000);
        events = eventsPage.getData().stream().filter(filterByPostTelemetryEventType()).collect(Collectors.toList());


        Assert.assertEquals(2, events.size());

        inEvent = events.stream().filter(e -> e.getBody().get("type").asText().equals(DataConstants.IN)).findFirst().get();
        Assert.assertEquals(lastRuleNode.getId(), inEvent.getEntityId());
        Assert.assertEquals(device.getId().getId().toString(), inEvent.getBody().get("entityId").asText());

        outEvent = events.stream().filter(e -> e.getBody().get("type").asText().equals(DataConstants.OUT)).findFirst().get();
        Assert.assertEquals(lastRuleNode.getId(), outEvent.getEntityId());
        Assert.assertEquals(device.getId().getId().toString(), outEvent.getBody().get("entityId").asText());

        Assert.assertEquals("serverAttributeValue1", getMetadata(outEvent).get("ss_serverAttributeKey1").asText());
        Assert.assertEquals("serverAttributeValue2", getMetadata(outEvent).get("ss_serverAttributeKey2").asText());
    }

}
