
package org.thingsboard.server.transport.lwm2m.server.store.util;

import com.eclipsesource.json.JsonObject;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.leshan.core.request.Identity;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LwM2MIdentitySerDesTest {

    @Test
    void serializePskIdentity() {
        assertThat(LwM2MIdentitySerDes.serialize(Identity.psk(getTestAddress(), "my:psk")).toString())
                .isEqualTo("{\"type\":\"psk\",\"id\":\"my:psk\"}");
    }


    @Test
    void serializeRpkIdentity() {
        var public_key = mock(PublicKey.class);
        when(public_key.getEncoded()).thenReturn(new byte[]{1,2,3,4,5,6,7,8,9});

        assertThat(LwM2MIdentitySerDes.serialize(Identity.rpk(getTestAddress(), public_key)).toString())
                .isEqualTo("{\"type\":\"rpk\",\"rpk\":\"010203040506070809\"}");
    }

    @Test
    void serializeX509Identity() {
        assertThat(LwM2MIdentitySerDes.serialize(Identity.x509(getTestAddress(), "MyCommonName")).toString())
                .isEqualTo("{\"type\":\"x509\",\"cn\":\"MyCommonName\"}");
    }

    @Test
    void serializeUnsecureIdentity() {
        assertThat(LwM2MIdentitySerDes.serialize(Identity.unsecure(getTestAddress())).toString())
                .isEqualTo("{\"type\":\"unsecure\",\"address\":\"1.2.3.4\",\"port\":5684}");
    }
    

    @Test
    void deserialize() {
        assertThatThrownBy(() -> LwM2MIdentitySerDes.deserialize(mock(JsonObject.class)))
                .isInstanceOf(NotImplementedException.class);
    }

    private static InetSocketAddress getTestAddress() {
        try {
            return new InetSocketAddress(InetAddress.getByName("1.2.3.4"), 5684);
        } catch (UnknownHostException e) {
            throw new AssertionError("Cannot create test address");
        }
    }
}