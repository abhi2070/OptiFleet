
package org.thingsboard.server.service.security.auth.oauth2;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.oauth2.OAuth2MapperConfig;
import org.thingsboard.server.common.data.oauth2.OAuth2Registration;
import org.thingsboard.server.dao.oauth2.OAuth2User;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service(value = "appleOAuth2ClientMapper")
@Slf4j
@TbCoreComponent
public class AppleOAuth2ClientMapper extends AbstractOAuth2ClientMapper implements OAuth2ClientMapper {

    private static final String USER = "user";
    private static final String NAME = "name";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "email";

    @Override
    public SecurityUser getOrCreateUserByClientPrincipal(HttpServletRequest request, OAuth2AuthenticationToken token, String providerAccessToken, OAuth2Registration registration) {
        OAuth2MapperConfig config = registration.getMapperConfig();
        Map<String, Object> attributes = updateAttributesFromRequestParams(request, token.getPrincipal().getAttributes());
        String email = BasicMapperUtils.getStringAttributeByKey(attributes, config.getBasic().getEmailAttributeKey());
        OAuth2User oauth2User = BasicMapperUtils.getOAuth2User(email, attributes, config);

        return getOrCreateSecurityUserFromOAuth2User(oauth2User, registration);
    }

    private static Map<String, Object> updateAttributesFromRequestParams(HttpServletRequest request, Map<String, Object> attributes) {
        Map<String, Object> updated = attributes;
        MultiValueMap<String, String> params = toMultiMap(request.getParameterMap());
        String userValue = params.getFirst(USER);
        if (StringUtils.hasText(userValue)) {
            JsonNode user = null;
            try {
                user = JacksonUtil.toJsonNode(userValue);
            } catch (Exception e) {}
            if (user != null) {
                updated = new HashMap<>(attributes);
                if (user.has(NAME)) {
                    JsonNode name = user.get(NAME);
                    if (name.isObject()) {
                        JsonNode firstName = name.get(FIRST_NAME);
                        if (firstName != null && firstName.isTextual()) {
                            updated.put(FIRST_NAME, firstName.asText());
                        }
                        JsonNode lastName = name.get(LAST_NAME);
                        if (lastName != null && lastName.isTextual()) {
                            updated.put(LAST_NAME, lastName.asText());
                        }
                    }
                }
                if (user.has(EMAIL)) {
                    JsonNode email = user.get(EMAIL);
                    if (email != null && email.isTextual()) {
                        updated.put(EMAIL, email.asText());
                    }
                }
            }
        }
        return updated;
    }

    private static MultiValueMap<String, String> toMultiMap(Map<String, String[]> map) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(map.size());
        map.forEach((key, values) -> {
            if (values.length > 0) {
                for (String value : values) {
                    params.add(key, value);
                }
            }
        });
        return params;
    }
}
