
package org.thingsboard.server.dao.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.dao.widget.WidgetTypeDao;
import org.thingsboard.server.dao.widget.WidgetsBundleDao;

import java.util.UUID;

import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = WidgetTypeDataValidator.class)
class WidgetTypeDataValidatorTest {
    @MockBean
    WidgetTypeDao widgetTypeDao;
    @MockBean
    WidgetsBundleDao widgetsBundleDao;
    @MockBean
    TenantService tenantService;
    @SpyBean
    WidgetTypeDataValidator validator;
    TenantId tenantId = TenantId.fromUUID(UUID.fromString("9ef79cdf-37a8-4119-b682-2e7ed4e018da"));

    @BeforeEach
    void setUp() {
        willReturn(true).given(tenantService).tenantExists(tenantId);
    }

    @Test
    void testValidateNameInvocation() {
        WidgetTypeDetails widgetTypeDetails = new WidgetTypeDetails();
        widgetTypeDetails.setName("widget type gas");
        widgetTypeDetails.setDescriptor(JacksonUtil.toJsonNode("{\"content\":\"empty\"}"));
        widgetTypeDetails.setTenantId(tenantId);

        validator.validateDataImpl(tenantId, widgetTypeDetails);
        verify(validator).validateString("Widgets type name", widgetTypeDetails.getName());
    }

}
