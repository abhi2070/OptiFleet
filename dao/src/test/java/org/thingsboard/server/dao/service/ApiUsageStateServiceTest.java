
package org.thingsboard.server.dao.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.thingsboard.server.common.data.ApiUsageState;
import org.thingsboard.server.common.data.ApiUsageStateValue;
import org.thingsboard.server.dao.usagerecord.ApiUsageStateService;


@DaoSqlTest
public class ApiUsageStateServiceTest extends AbstractServiceTest {

    @Autowired
    ApiUsageStateService apiUsageStateService;

    @Test
    public void testFindApiUsageStateByTenantId() {
        ApiUsageState apiUsageState = apiUsageStateService.findTenantApiUsageState(tenantId);
        Assert.assertNotNull(apiUsageState);
    }

    @Test
    public void testUpdateApiUsageState(){
        ApiUsageState apiUsageState = apiUsageStateService.findTenantApiUsageState(tenantId);
        Assert.assertNotNull(apiUsageState);
        Assert.assertTrue(apiUsageState.isTransportEnabled());
        apiUsageState.setTransportState(ApiUsageStateValue.DISABLED);
        apiUsageState = apiUsageStateService.update(apiUsageState);
        Assert.assertNotNull(apiUsageState);
        apiUsageState = apiUsageStateService.findTenantApiUsageState(tenantId);
        Assert.assertNotNull(apiUsageState);
        Assert.assertFalse(apiUsageState.isTransportEnabled());
    }

}
