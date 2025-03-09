package org.thingsboard.server.service.self_registration;

import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.model.RegistrationSettings;

public interface SelfRegistrationService {

    RegistrationSettings getRegistrationSettings();

    RegistrationSettings saveRegistrationSettings(RegistrationSettings registrationSettings);

    boolean deleteRegistrationSettings(TenantId tenantId);
}
