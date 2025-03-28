
package org.thingsboard.server.common.data.security.model.mfa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.thingsboard.server.common.data.security.model.mfa.provider.TwoFaProviderConfig;
import org.thingsboard.server.common.data.security.model.mfa.provider.TwoFaProviderType;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Optional;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformTwoFaSettings {

    @Valid
    @NotNull
    private List<TwoFaProviderConfig> providers;

    @NotNull
    @Min(value = 5)
    private Integer minVerificationCodeSendPeriod;
    @Pattern(regexp = "[1-9]\\d*:[1-9]\\d*", message = "is invalid")
    private String verificationCodeCheckRateLimit;
    @Min(value = 0, message = "must be positive")
    private Integer maxVerificationFailuresBeforeUserLockout;
    @NotNull
    @Min(value = 60)
    private Integer totalAllowedTimeForVerification;


    public Optional<TwoFaProviderConfig> getProviderConfig(TwoFaProviderType providerType) {
        return Optional.ofNullable(providers)
                .flatMap(providersConfigs -> providersConfigs.stream()
                        .filter(providerConfig -> providerConfig.getProviderType() == providerType)
                        .findFirst());
    }

}
