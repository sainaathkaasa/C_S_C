package com.example.client.config;

import com.example.client.handler.ClientWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.net.URISyntaxException;

@Configuration
@EnableWebSocket
public class ClientWebSocketConfig implements WebSocketConfigurer {

    @Value("${server.url}")
    private String serverUrl;

    public String getServerUrl() {
        return serverUrl;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        try {
            registry.addHandler(new ClientWebSocketHandler(), "/client/ws").setAllowedOrigins("*");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
