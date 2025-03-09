
package org.thingsboard.server.transport.mqtt.session;

import org.junit.jupiter.api.Test;
import org.springframework.util.ConcurrentReferenceHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willCallRealMethod;
import static org.mockito.Mockito.mock;

public class GatewaySessionHandlerTest {

    @Test
    public void givenGatewaySessionHandler_WhenCreateWeakMap_thenConcurrentReferenceHashMapClass() {
        GatewaySessionHandler gsh = mock(GatewaySessionHandler.class);
        willCallRealMethod().given(gsh).createWeakMap();

        assertThat(gsh.createWeakMap()).isInstanceOf(ConcurrentReferenceHashMap.class);
    }

}
