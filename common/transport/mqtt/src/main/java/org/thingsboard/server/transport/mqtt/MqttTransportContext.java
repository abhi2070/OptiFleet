
package org.thingsboard.server.transport.mqtt;

import io.netty.handler.ssl.SslHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.transport.TransportContext;
import org.thingsboard.server.transport.mqtt.adaptors.JsonMqttAdaptor;
import org.thingsboard.server.transport.mqtt.adaptors.ProtoMqttAdaptor;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ashvayka on 04.10.18.
 */
@Slf4j
@Component
@ConditionalOnExpression("'${service.type:null}'=='tb-transport' || ('${service.type:null}'=='monolith' && '${transport.api_enabled:true}'=='true' && '${transport.mqtt.enabled}'=='true')")
public class MqttTransportContext extends TransportContext {

    @Getter
    @Autowired(required = false)
    private MqttSslHandlerProvider sslHandlerProvider;

    @Getter
    @Autowired
    private JsonMqttAdaptor jsonMqttAdaptor;

    @Getter
    @Autowired
    private ProtoMqttAdaptor protoMqttAdaptor;

    @Getter
    @Value("${transport.mqtt.netty.max_payload_size}")
    private Integer maxPayloadSize;

    @Getter
    @Value("${transport.mqtt.ssl.skip_validity_check_for_client_cert:false}")
    private boolean skipValidityCheckForClientCert;

    @Getter
    @Setter
    private SslHandler sslHandler;

    @Getter
    @Value("${transport.mqtt.msg_queue_size_per_device_limit:100}")
    private int messageQueueSizePerDeviceLimit;

    @Getter
    @Value("${transport.mqtt.timeout:10000}")
    private long timeout;

    @Getter
    @Value("${transport.mqtt.proxy_enabled:false}")
    private boolean proxyEnabled;

    private final AtomicInteger connectionsCounter = new AtomicInteger();

    @PostConstruct
    public void init() {
        super.init();
        transportService.createGaugeStats("openConnections", connectionsCounter);
    }

    public void channelRegistered() {
        connectionsCounter.incrementAndGet();
    }

    public void channelUnregistered() {
        connectionsCounter.decrementAndGet();
    }

    public boolean checkAddress(InetSocketAddress address) {
        return rateLimitService.checkAddress(address);
    }

    public void onAuthSuccess(InetSocketAddress address) {
        rateLimitService.onAuthSuccess(address);
    }

    public void onAuthFailure(InetSocketAddress address) {
        rateLimitService.onAuthFailure(address);
    }

}
