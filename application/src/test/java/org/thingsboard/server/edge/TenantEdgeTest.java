
package org.thingsboard.server.edge;

import org.junit.Assert;
import org.junit.Test;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.dao.service.DaoSqlTest;
import org.thingsboard.server.gen.edge.v1.TenantProfileUpdateMsg;
import org.thingsboard.server.gen.edge.v1.TenantUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;

import java.util.Optional;

@DaoSqlTest
public class TenantEdgeTest extends AbstractEdgeTest {

    @Test
    public void testUpdateTenant() throws Exception {
        loginSysAdmin();

        // save current value into tmp to revert after test
        Tenant savedTenant = doGet("/api/tenant/" + tenantId, Tenant.class);

        // updated edge tenant
        savedTenant.setTitle("Updated Title for Tenant Edge Test");
        edgeImitator.expectMessageAmount(2); // expect tenant and tenant profile update msg
        savedTenant = doPost("/api/tenant", savedTenant, Tenant.class);
        Assert.assertTrue(edgeImitator.waitForMessages());
        Optional<TenantUpdateMsg> tenantUpdateMsgOpt = edgeImitator.findMessageByType(TenantUpdateMsg.class);
        Assert.assertTrue(tenantUpdateMsgOpt.isPresent());
        TenantUpdateMsg tenantUpdateMsg = tenantUpdateMsgOpt.get();
        Optional<TenantProfileUpdateMsg> tenantProfileUpdateMsgOpt = edgeImitator.findMessageByType(TenantProfileUpdateMsg.class);
        Assert.assertTrue(tenantProfileUpdateMsgOpt.isPresent());
        TenantProfileUpdateMsg tenantProfileUpdateMsg = tenantProfileUpdateMsgOpt.get();
        Tenant tenantMsg = JacksonUtil.fromString(tenantUpdateMsg.getEntity(), Tenant.class, true);
        Assert.assertNotNull(tenantMsg);
        Assert.assertEquals(savedTenant, tenantMsg);
        TenantProfile tenantProfileMsg = JacksonUtil.fromString(tenantProfileUpdateMsg.getEntity(), TenantProfile.class, true);
        Assert.assertNotNull(tenantProfileMsg);
        Assert.assertEquals(tenantMsg.getTenantProfileId(), tenantProfileMsg.getId());
        Assert.assertEquals(UpdateMsgType.ENTITY_UPDATED_RPC_MESSAGE, tenantUpdateMsg.getMsgType());
        Assert.assertEquals("Updated Title for Tenant Edge Test", tenantMsg.getTitle());

        //change tenant profile for tenant
        TenantProfile tenantProfile = createTenantProfile();
        savedTenant.setTenantProfileId(tenantProfile.getId());
        edgeImitator.expectMessageAmount(2); // expect tenant and tenant profile update msg
        savedTenant = doPost("/api/tenant", savedTenant, Tenant.class);
        Assert.assertTrue(edgeImitator.waitForMessages());
        tenantUpdateMsgOpt = edgeImitator.findMessageByType(TenantUpdateMsg.class);
        Assert.assertTrue(tenantUpdateMsgOpt.isPresent());
        tenantUpdateMsg = tenantUpdateMsgOpt.get();
        tenantMsg = JacksonUtil.fromString(tenantUpdateMsg.getEntity(), Tenant.class, true);
        Assert.assertNotNull(tenantMsg);
        tenantProfileUpdateMsgOpt = edgeImitator.findMessageByType(TenantProfileUpdateMsg.class);
        Assert.assertTrue(tenantProfileUpdateMsgOpt.isPresent());
        tenantProfileUpdateMsg = tenantProfileUpdateMsgOpt.get();
        tenantProfileMsg = JacksonUtil.fromString(tenantProfileUpdateMsg.getEntity(), TenantProfile.class, true);
        Assert.assertNotNull(tenantProfileMsg);
        // tenant update
        Assert.assertEquals(UpdateMsgType.ENTITY_UPDATED_RPC_MESSAGE, tenantUpdateMsg.getMsgType());
        Assert.assertEquals(savedTenant, tenantMsg);
        Assert.assertEquals(savedTenant.getTenantProfileId(), tenantProfileMsg.getId());
    }

    private TenantProfile createTenantProfile() {
        TenantProfile tenantProfile = new TenantProfile();
        tenantProfile.setName("TestEdge tenant profile");
        return doPost("/api/tenantProfile", tenantProfile, TenantProfile.class);
    }
}
