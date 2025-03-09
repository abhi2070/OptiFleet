
package org.thingsboard.server.dao.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.thingsboard.server.common.data.EntityView;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.entityview.EntityViewDao;
import org.thingsboard.server.dao.tenant.TenantService;

import java.util.UUID;

import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = EntityViewDataValidator.class)
class EntityViewDataValidatorTest {

    @MockBean
    EntityViewDao entityViewDao;
    @MockBean
    TenantService tenantService;
    @MockBean
    CustomerDao customerDao;
    @SpyBean
    EntityViewDataValidator validator;
    TenantId tenantId = TenantId.fromUUID(UUID.fromString("9ef79cdf-37a8-4119-b682-2e7ed4e018da"));

    @BeforeEach
    void setUp() {
        willReturn(true).given(tenantService).tenantExists(tenantId);
    }

    @Test
    void testValidateNameInvocation() {
        EntityView entityView = new EntityView();
        entityView.setName("view");
        entityView.setType("default");
        entityView.setTenantId(tenantId);

        validator.validateDataImpl(tenantId, entityView);
        verify(validator).validateString("Entity view name", entityView.getName());
        verify(validator).validateString("Entity view type", entityView.getType());
    }

}
