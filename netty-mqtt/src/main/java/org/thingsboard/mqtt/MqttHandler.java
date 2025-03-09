
package org.thingsboard.mqtt;

import com.google.common.util.concurrent.ListenableFuture;
import io.netty.buffer.ByteBuf;

public interface MqttHandler {

    ListenableFuture<Void> onMessage(String topic, ByteBuf payload);
}
