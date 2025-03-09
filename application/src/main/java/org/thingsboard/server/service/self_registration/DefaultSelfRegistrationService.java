package org.thingsboard.server.service.self_registration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.model.*;
import org.thingsboard.server.dao.settings.AdminSettingsService;

@Service
@Slf4j
public class DefaultSelfRegistrationService implements SelfRegistrationService {

    @Autowired
    private AdminSettingsService adminSettingsService;

    @Override
    public RegistrationSettings getRegistrationSettings() {
        RegistrationSettings registrationSettings = null;
        AdminSettings adminSettings = adminSettingsService.findAdminSettingsByKey(TenantId.SYS_TENANT_ID, "selfRegistrationSettings");
        if(adminSettings != null) {
            try {
                registrationSettings = JacksonUtil.convertValue(adminSettings.getJsonValue(), RegistrationSettings.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load self registration settings!", e);
            }
        } else {
            registrationSettings = new RegistrationSettings();
            registrationSettings.setDomainSettings(new DomainSettings());
            registrationSettings.setGeneralSettings(new GeneralSettings());
            registrationSettings.setMobileSettings(new MobileSettings());
        }
        return registrationSettings;
    }

    @Override
    public RegistrationSettings saveRegistrationSettings(RegistrationSettings registrationSettings) {
        AdminSettings adminSettings = adminSettingsService.findAdminSettingsByKey(TenantId.SYS_TENANT_ID, "selfRegistrationSettings");
        log.info(String.valueOf(adminSettings));
        if(adminSettings == null) {
            adminSettings = new AdminSettings();
            adminSettings.setTenantId(TenantId.SYS_TENANT_ID);
            adminSettings.setKey("selfRegistrationSettings");
        }
        adminSettings.setJsonValue(JacksonUtil.valueToTree(registrationSettings));
        AdminSettings savedAdminSettings = adminSettingsService.saveAdminSettings(TenantId.SYS_TENANT_ID, adminSettings);
        try {
            return JacksonUtil.convertValue(savedAdminSettings.getJsonValue(), RegistrationSettings.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load self registration settings!", e);
        }
    }

    @Override
    public boolean deleteRegistrationSettings(TenantId tenantId) {
        boolean result = adminSettingsService.deleteAdminSettingsByTenantIdAndKey(tenantId, "selfRegistrationSettings");
        return result;
    }

}
