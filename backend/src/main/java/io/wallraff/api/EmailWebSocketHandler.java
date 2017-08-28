package io.wallraff.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wallraff.data.EmailRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class EmailWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final EmailWebSocketRoute emailWebSocketRoute;

    public EmailWebSocketHandler(EmailWebSocketRoute emailWebSocketRoute) {
        this.emailWebSocketRoute = emailWebSocketRoute;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void broadcastNewEmailMessage(EmailRecord email) throws IOException {
        final String content = new ObjectMapper().writeValueAsString(email);

        for (WebSocketSession webSocketSession : sessions) {
            String inbox = emailWebSocketRoute.extractInboxName(webSocketSession.getUri());
            if (email.getInbox().equals(inbox)) {
                webSocketSession.sendMessage(new TextMessage(content));
            }
        }
    }

}
