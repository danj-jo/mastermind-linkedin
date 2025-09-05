package com.example.mastermind.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
/**
 * Configuration class for WebSocket and STOMP messaging in multiplayer games.
 * <p>
 * Enables real-time communication between players using WebSocket connections
 * with STOMP protocol. Configures message broker with "/topic" destination
 * prefix for broadcasting game events to all connected players, and "/app"
 * prefix for client-to-server message publishing.
 * 
 */
@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebsocketChannel_Interceptor websocketChannelInterceptor;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic/mp");
        config.setApplicationDestinationPrefixes("/app");

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint for clients to connect. Allow SockJS fallback for browsers/environments without native WS.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    /**
     * Here, I set my interceptor to receive inbound STOMP messages to my websocket connection. This allows me to extract important data.
     * @param registration - registration used to attach interceptors.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(websocketChannelInterceptor);
    }
}
