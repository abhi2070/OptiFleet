
package org.thingsboard.server.common.data.security.model.mfa.account;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.security.model.mfa.provider.TwoFaProviderType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@EqualsAndHashCode(callSuper = true)
public class TotpTwoFaAccountConfig extends TwoFaAccountConfig {

    @NotBlank
    @Pattern(regexp = "otpauth://totp/(\\S+?):(\\S+?)\\?issuer=(\\S+?)&secret=(\\w+?)", message = "is invalid")
    private String authUrl;

    @Override
    public TwoFaProviderType getProviderType() {
        return TwoFaProviderType.TOTP;
    }

}

