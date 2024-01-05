package com.github;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
    }

    @Override
    public void registerStompEndpoints(org.springframework.web.socket.config.annotation.StompEndpointRegistry registry) {
        registry.addEndpoint("/game").setAllowedOrigins("http://localhost:3000", "https://master--timely-sundae-a39916.netlify.app/");
        registry.addEndpoint("/game/").setAllowedOrigins("http://localhost:3000", "https://master--timely-sundae-a39916.netlify.app/");
        registry.addEndpoint("/test").setAllowedOrigins("http://localhost:3000", "https://master--timely-sundae-a39916.netlify.app/");
        registry.addEndpoint("/debug").setAllowedOrigins("http://localhost:3000", "https://master--timely-sundae-a39916.netlify.app/");
        registry.addEndpoint("/lobby").setAllowedOrigins("http://localhost:3000");
        registry.addEndpoint("/lobby/{id}").setAllowedOrigins("http://localhost:3000");
    }
}
