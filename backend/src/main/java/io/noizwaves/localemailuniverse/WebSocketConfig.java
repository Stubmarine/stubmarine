package io.noizwaves.localemailuniverse;

import io.noizwaves.localemailuniverse.api.EmailWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(emailWebSocketHandler(), "/wsapi/emails").setAllowedOrigins("*");
    }

    @Bean
    public EmailWebSocketHandler emailWebSocketHandler() {
        return new EmailWebSocketHandler();
    }
}
