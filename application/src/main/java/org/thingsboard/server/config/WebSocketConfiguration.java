
package org.thingsboard.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.thingsboard.server.controller.plugin.TbWebSocketHandler;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Configuration
@TbCoreComponent
@EnableWebSocket
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfiguration implements WebSocketConfigurer {

    public static final String WS_API_ENDPOINT = "/api/ws";
    public static final String WS_PLUGINS_ENDPOINT = "/api/ws/plugins/";
    private static final String WS_API_MAPPING = "/api/ws/**";

    private final WebSocketHandler wsHandler;

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(32768);
        container.setMaxBinaryMessageBufferSize(32768);
        return container;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        if (!(wsHandler instanceof TbWebSocketHandler)) {
            log.error("TbWebSocketHandler expected but [{}] provided", wsHandler);
            throw new RuntimeException("TbWebSocketHandler expected but " + wsHandler + " provided");
        }
        registry.addHandler(wsHandler, WS_API_MAPPING).setAllowedOriginPatterns("*");
    }

}
