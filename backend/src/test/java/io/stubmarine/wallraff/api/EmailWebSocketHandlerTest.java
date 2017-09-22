package io.stubmarine.wallraff.api;

import io.stubmarine.wallraff.data.EmailRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.net.URISyntaxException;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailWebSocketHandlerTest {

    @InjectMocks
    private EmailWebSocketHandler handler;

    @Mock
    private EmailWebSocketRoute emailWebSocketRoute;

    @Captor
    private ArgumentCaptor<TextMessage> messageCaptor;

    @Test
    public void testBroadcastNewEmailMessage() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        URI path = mockUri();
        when(session.getUri()).thenReturn(path);

        when(emailWebSocketRoute.extractInboxName(path)).thenReturn("zoo");

        handler.afterConnectionEstablished(session);


        handler.broadcastNewEmailMessage(
                new EmailRecord(4, "dog", "kitty", "app", "Silent", "Lazy Dog", "jump jump", "zoo")
        );


        verify(session, times(1)).sendMessage(messageCaptor.capture());

        String payload = messageCaptor.getValue().getPayload();
        with(payload)
                .assertThat("$.*", hasSize(7))
                .assertThat("$.id", equalTo(4))
                .assertThat("$.from", equalTo("dog"))
                .assertThat("$.to", equalTo("kitty"))
                .assertThat("$.cc", equalTo("app"))
                .assertThat("$.bcc", equalTo("Silent"))
                .assertThat("$.subject", equalTo("Lazy Dog"))
                .assertThat("$.body", equalTo("jump jump"));
    }

    @Test
    public void testBroadcastNewEmailMessage_subscriberOnDifferentInbox() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        URI path = mockUri();
        when(session.getUri()).thenReturn(path);

        when(emailWebSocketRoute.extractInboxName(path)).thenReturn("fab");

        handler.afterConnectionEstablished(session);


        handler.broadcastNewEmailMessage(
                new EmailRecord(4, "dog", "kitty", "app", "silent", "Lazy Dog", "jump jump", "zoo")
        );


        verify(session, times(0)).sendMessage(any());
    }

    @Test
    public void testBroadcastNewEmailMessage_disconnection() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        handler.afterConnectionEstablished(session);
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);


        handler.broadcastNewEmailMessage(
                new EmailRecord(4, "dog", "kitty", "app", "silent", "Lazy Dog", "jump jump", "zoo")
        );


        verify(session, times(0)).sendMessage(any());
    }

    private static URI mockUri() throws URISyntaxException {
        return new URI(null, null, "a/unique/mocked/uri", null);
    }
}