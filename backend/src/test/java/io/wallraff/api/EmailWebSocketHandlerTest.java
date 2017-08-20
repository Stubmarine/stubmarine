package io.wallraff.api;

import io.wallraff.data.EmailRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmailWebSocketHandlerTest {

    @InjectMocks
    private EmailWebSocketHandler handler;

    @Captor
    private ArgumentCaptor<TextMessage> messageCaptor;

    @Test
    public void testBroadcastNewEmailMessage() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        handler.afterConnectionEstablished(session);


        handler.broadcastNewEmailMessage(
                new EmailRecord(4, "dog", "kitty", "Lazy Dog", "jump jump")
        );


        verify(session, times(1)).sendMessage(messageCaptor.capture());

        String payload = messageCaptor.getValue().getPayload();
        with(payload)
                .assertThat("$.*", hasSize(5))
                .assertThat("$.id", equalTo(4))
                .assertThat("$.from", equalTo("dog"))
                .assertThat("$.to", equalTo("kitty"))
                .assertThat("$.subject", equalTo("Lazy Dog"))
                .assertThat("$.body", equalTo("jump jump"));
    }

    @Test
    public void testBroadcastNewEmailMessage_disconnection() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        handler.afterConnectionEstablished(session);
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);


        handler.broadcastNewEmailMessage(
                new EmailRecord(4, "dog", "kitty", "Lazy Dog", "jump jump")
        );


        verify(session, times(0)).sendMessage(any());
    }
}