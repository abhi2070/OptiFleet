
package org.thingsboard.server.service.mail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.settings.AdminSettingsService;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.thingsboard.server.common.data.mail.MailOauth2Provider.OFFICE_365;

@TbCoreComponent
@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenExpCheckService {
    public static final int AZURE_DEFAULT_REFRESH_TOKEN_LIFETIME_IN_DAYS = 90;
    private final AdminSettingsService adminSettingsService;

    @Scheduled(initialDelayString = "#{T(org.apache.commons.lang3.RandomUtils).nextLong(0, ${mail.oauth2.refreshTokenCheckingInterval})}", fixedDelayString = "${mail.oauth2.refreshTokenCheckingInterval}")
    public void check() throws IOException {
        AdminSettings settings = adminSettingsService.findAdminSettingsByKey(TenantId.SYS_TENANT_ID, "mail");
        if (settings != null && settings.getJsonValue().has("enableOauth2") && settings.getJsonValue().get("enableOauth2").asBoolean()) {
            JsonNode jsonValue = settings.getJsonValue();
            if (OFFICE_365.name().equals(jsonValue.get("providerId").asText()) && jsonValue.has("refreshTokenExpires")) {
                long expiresIn = jsonValue.get("refreshTokenExpires").longValue();
                if ((expiresIn - System.currentTimeMillis()) < 604800000L) { //less than 7 days
                    log.info("Trying to refresh refresh token.");

                    String clientId = jsonValue.get("clientId").asText();
                    String clientSecret = jsonValue.get("clientSecret").asText();
                    String refreshToken = jsonValue.get("refreshToken").asText();
                    String tokenUri = jsonValue.get("tokenUri").asText();

                    TokenResponse tokenResponse = new RefreshTokenRequest(new NetHttpTransport(), new GsonFactory(),
                            new GenericUrl(tokenUri), refreshToken)
                            .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                            .execute();
                    ((ObjectNode)jsonValue).put("refreshToken", tokenResponse.getRefreshToken());
                    ((ObjectNode)jsonValue).put("refreshTokenExpires", Instant.now().plus(Duration.ofDays(AZURE_DEFAULT_REFRESH_TOKEN_LIFETIME_IN_DAYS)).toEpochMilli());
                    adminSettingsService.saveAdminSettings(TenantId.SYS_TENANT_ID, settings);
                }
            }
        }
    }
}