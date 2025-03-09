
package org.thingsboard.server.common.data.security.model.mfa.provider;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TotpTwoFaProviderConfig implements TwoFaProviderConfig {

    @NotBlank
    private String issuerName;

    @Override
    public TwoFaProviderType getProviderType() {
        return TwoFaProviderType.TOTP;
    }

}
