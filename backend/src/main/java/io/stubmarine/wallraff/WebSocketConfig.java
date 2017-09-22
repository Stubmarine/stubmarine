package io.stubmarine.wallraff;

import io.stubmarine.wallraff.api.EmailWebSocketHandler;
import io.stubmarine.wallraff.api.EmailWebSocketRoute;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final EmailWebSocketHandler emailWebSocketHandler;
    private final EmailWebSocketRoute emailWebSocketRoute;

    public WebSocketConfig(
            EmailWebSocketHandler emailWebSocketHandler,
            EmailWebSocketRoute emailWebSocketRoute
    ) {
        this.emailWebSocketHandler = emailWebSocketHandler;
        this.emailWebSocketRoute = emailWebSocketRoute;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(emailWebSocketHandler, emailWebSocketRoute.getPath())
                .setAllowedOrigins("*");
    }
}
