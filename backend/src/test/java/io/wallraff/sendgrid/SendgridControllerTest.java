package io.wallraff.sendgrid;

import io.wallraff.api.EmailWebSocketHandler;
import io.wallraff.data.EmailRecord;
import io.wallraff.data.EmailRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class SendgridControllerTest {

    @InjectMocks
    private SendgridController controller;

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private EmailWebSocketHandler emailWebSocketHandler;

    @Mock
    private SendGridTokenVerifier sendGridTokenVerifier;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testMailSend() throws Exception {
        EmailRecord savedRecord = mock(EmailRecord.class);
        when(emailRepository.save(new EmailRecord(
                null,
                "sender@example.com",
                "to@example.com",
                "My subjecT",
                "Content content content!"
        ))).thenReturn(savedRecord);

        when(sendGridTokenVerifier.verify(any())).thenReturn(true);

        String content = "" +
                "{" +
                "  \"personalizations\": [{" +
                "    \"to\": [{" +
                "      \"email\": \"to@example.com\"" +
                "    }]" +
                "  }]," +
                "  \"subject\": \"My subjecT\"," +
                "  \"from\": {" +
                "    \"email\": \"sender@example.com\"" +
                "  }," +
                "  \"content\": [{" +
                "    \"type\": \"text/plain\"," +
                "    \"value\": \"Content content content!\"" +
                "  }]" +
                "}";

        ResultActions resultActions = mockMvc.perform(
                post("/eapi/sendgrid/v3/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer foobar")
        );


        resultActions
                .andExpect(status().isAccepted())
                .andExpect(content().string(""));

        verify(emailWebSocketHandler).broadcastNewEmailMessage(savedRecord);

        verify(sendGridTokenVerifier).verify("foobar");
    }

    @Test
    public void testMailSend_MultipleRecipients() throws Exception {
        EmailRecord savedRecord = mock(EmailRecord.class);
        when(emailRepository.save(new EmailRecord(
                null,
                "sender@example.com",
                "to1@example.com, to2@example.com, to3@example.com",
                "My subjecT",
                "Content content content!"
        ))).thenReturn(savedRecord);

        when(sendGridTokenVerifier.verify(any())).thenReturn(true);

        String content = "" +
                "{" +
                "  \"personalizations\": [{" +
                "    \"to\": [{" +
                "      \"email\": \"to1@example.com\"" +
                "    }, {" +
                "      \"email\": \"to2@example.com\"" +
                "    }" +
                "  ]}, {" +
                "    \"to\": [{" +
                "      \"email\": \"to3@example.com\"" +
                "    }]" +
                "  }]," +
                "  \"subject\": \"My subjecT\"," +
                "  \"from\": {" +
                "    \"email\": \"sender@example.com\"" +
                "  }," +
                "  \"content\": [{" +
                "    \"type\": \"text/plain\"," +
                "    \"value\": \"Content content content!\"" +
                "  }]" +
                "}";


        mockMvc.perform(
                post("/eapi/sendgrid/v3/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer foobar")
        );


        verify(emailWebSocketHandler).broadcastNewEmailMessage(savedRecord);
    }

    @Test
    public void testMailSend_InvalidToken() throws Exception {
        EmailRecord savedRecord = mock(EmailRecord.class);
        when(emailRepository.save(new EmailRecord(
                null,
                "sender@example.com",
                "to@example.com",
                "My subjecT",
                "Content content content!"
        ))).thenReturn(savedRecord);

        when(sendGridTokenVerifier.verify(any())).thenReturn(false);

        String content = "" +
                "{" +
                "  \"personalizations\": [{" +
                "    \"to\": [{" +
                "      \"email\": \"to@example.com\"" +
                "    }]" +
                "  }]," +
                "  \"subject\": \"My subjecT\"," +
                "  \"from\": {" +
                "    \"email\": \"sender@example.com\"" +
                "  }," +
                "  \"content\": [{" +
                "    \"type\": \"text/plain\"," +
                "    \"value\": \"Content content content!\"" +
                "  }]" +
                "}";

        ResultActions resultActions = mockMvc.perform(
                post("/eapi/sendgrid/v3/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer foobar")
        );


        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));

        verifyZeroInteractions(emailRepository);
        verifyZeroInteractions(emailWebSocketHandler);
    }

    @Test
    public void testMailSend_NonBearerAuthorization() throws Exception {
        String content = "" +
                "{" +
                "  \"personalizations\": [{" +
                "    \"to\": [{" +
                "      \"email\": \"to@example.com\"" +
                "    }]" +
                "  }]," +
                "  \"subject\": \"My subjecT\"," +
                "  \"from\": {" +
                "    \"email\": \"sender@example.com\"" +
                "  }," +
                "  \"content\": [{" +
                "    \"type\": \"text/plain\"," +
                "    \"value\": \"Content content content!\"" +
                "  }]" +
                "}";

        ResultActions resultActions = mockMvc.perform(
                post("/eapi/sendgrid/v3/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Basic foo:bar")
        );


        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));

        verifyZeroInteractions(sendGridTokenVerifier);
        verifyZeroInteractions(emailRepository);
        verifyZeroInteractions(emailWebSocketHandler);
    }

    @Test
    public void testMailSend_NoAuthorizationHeader() throws Exception {
        String content = "" +
                "{" +
                "  \"personalizations\": [{" +
                "    \"to\": [{" +
                "      \"email\": \"to@example.com\"" +
                "    }]" +
                "  }]," +
                "  \"subject\": \"My subjecT\"," +
                "  \"from\": {" +
                "    \"email\": \"sender@example.com\"" +
                "  }," +
                "  \"content\": [{" +
                "    \"type\": \"text/plain\"," +
                "    \"value\": \"Content content content!\"" +
                "  }]" +
                "}";

        ResultActions resultActions = mockMvc.perform(
                post("/eapi/sendgrid/v3/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );


        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));

        verifyZeroInteractions(sendGridTokenVerifier);
        verifyZeroInteractions(emailRepository);
        verifyZeroInteractions(emailWebSocketHandler);
    }
}