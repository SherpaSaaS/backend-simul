package org.sherpaengineering.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("simulationHandler/websocket");
        registry.addEndpoint("simulationHandler/websocket").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        // Increase the maximum message size here
        registration.setMessageSizeLimit(1024 * 1024); // Set the maximum message size to 1MB
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");

    }
}