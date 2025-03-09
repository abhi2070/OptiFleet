
package org.thingsboard.server.service.security.auth.oauth2;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.thingsboard.server.service.security.auth.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME;

public class CookieUtilsTest {

    @Test
    public void serializeDeserializeOAuth2AuthorizationRequestTest() {
        HttpCookieOAuth2AuthorizationRequestRepository cookieRequestRepo = new HttpCookieOAuth2AuthorizationRequestRepository();
        HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);

        Map<String, Object> additionalParameters = new LinkedHashMap<>();
        additionalParameters.put("param1", "value1");
        additionalParameters.put("param2", "value2");
        var request = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri("testUri").clientId("testId")
                .scope("read", "write")
                .additionalParameters(additionalParameters).build();


        Cookie cookie = new Cookie(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtils.serialize(request));
        Mockito.when(servletRequest.getCookies()).thenReturn(new Cookie[]{cookie});

        OAuth2AuthorizationRequest deserializedRequest = cookieRequestRepo.loadAuthorizationRequest(servletRequest);

        assertNotNull(deserializedRequest);
        assertEquals(request.getGrantType(), deserializedRequest.getGrantType());
        assertEquals(request.getAuthorizationUri(), deserializedRequest.getAuthorizationUri());
        assertEquals(request.getClientId(), deserializedRequest.getClientId());
    }

}