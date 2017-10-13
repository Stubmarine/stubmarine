package io.stubmarine.sendgrid;

import io.stubmarine.data.EmailRepository;
import io.stubmarine.api.EmailWebSocketHandler;
import io.stubmarine.data.EmailRecord;
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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class SendGridControllerTest {

    @InjectMocks
    private SendGridController controller;

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private EmailWebSocketHandler emailWebSocketHandler;

    @Mock
    private SendGridTokenVerifier sendGridTokenVerifier;

    @Mock
    private SendGridEmailFactory sendGridEmailFactory;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testMailSend() throws Exception {
        EmailRecord generatedRecord = mock(EmailRecord.class);
        when(sendGridEmailFactory.getEmailsFromRequest(any(), any())).thenReturn(asList(generatedRecord));

        EmailRecord savedRecord = mock(EmailRecord.class);
        when(emailRepository.save(any(EmailRecord.class))).thenReturn(savedRecord);

        when(sendGridTokenVerifier.verify(any())).thenReturn(true);
        when(sendGridTokenVerifier.extractInbox(any())).thenReturn("zoo");

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

        verify(sendGridTokenVerifier).verify("foobar");
        verify(sendGridTokenVerifier).extractInbox("foobar");

        verify(sendGridEmailFactory).getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        asList(new AddressForm("to@example.com", null)),
                                        null,
                                        null
                                )
                        ),
                        asList(
                                new ContentForm("text/plain", "Content content content!")
                        )
                ),
                "zoo"
        );

        verify(emailRepository).save(generatedRecord);

        verify(emailWebSocketHandler).broadcastNewEmailMessage(savedRecord);

    }

    @Test
    public void testMailSend_MultipleRecipients() throws Exception {
        EmailRecord generatedRecord = mock(EmailRecord.class);
        when(sendGridEmailFactory.getEmailsFromRequest(any(), any())).thenReturn(asList(generatedRecord));

        EmailRecord savedRecord = mock(EmailRecord.class);
        when(emailRepository.save(generatedRecord)).thenReturn(savedRecord);

        when(sendGridTokenVerifier.verify(any())).thenReturn(true);
        when(sendGridTokenVerifier.extractInbox(any())).thenReturn("zoo");

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


        verify(sendGridEmailFactory).getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to1@example.com", null),
                                                new AddressForm("to2@example.com", null)
                                        ),
                                        null,
                                        null
                                ),
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to3@example.com", null)
                                        ),
                                        null,
                                        null
                                )
                        ),
                        asList(
                                new ContentForm("text/plain", "Content content content!")
                        )
                ),
                "zoo"
        );

        verify(emailWebSocketHandler).broadcastNewEmailMessage(savedRecord);
    }

    @Test
    public void testMailSend_CcRecipients() throws Exception {
        EmailRecord generatedRecord = mock(EmailRecord.class);
        when(sendGridEmailFactory.getEmailsFromRequest(any(), any())).thenReturn(asList(generatedRecord));

        EmailRecord savedRecord = mock(EmailRecord.class);
        when(emailRepository.save(generatedRecord)).thenReturn(savedRecord);

        when(sendGridTokenVerifier.verify(any())).thenReturn(true);
        when(sendGridTokenVerifier.extractInbox(any())).thenReturn("zoo");

        String content = "" +
                "{" +
                "  \"personalizations\": [{" +
                "      \"cc\": [" +
                "        {\"email\": \"cc@example.com\"}, " +
                "        {\"email\": \"cc2@example.com\", \"name\": \"Cecetwo\"}" +
                "      ] " +
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

        verify(sendGridEmailFactory).getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        null,
                                        asList(
                                                new AddressForm("cc@example.com", null),
                                                new AddressForm("cc2@example.com", "Cecetwo")
                                        ),
                                        null
                                )
                        ),
                        asList(
                                new ContentForm("text/plain", "Content content content!")
                        )
                ),
                "zoo"
        );

        verify(emailWebSocketHandler).broadcastNewEmailMessage(savedRecord);
    }

    @Test
    public void testMailSend_BccRecipients() throws Exception {
        EmailRecord generatedRecord = mock(EmailRecord.class);
        when(sendGridEmailFactory.getEmailsFromRequest(any(), any())).thenReturn(asList(generatedRecord));

        EmailRecord savedRecord = mock(EmailRecord.class);
        when(emailRepository.save(generatedRecord)).thenReturn(savedRecord);

        when(sendGridTokenVerifier.verify(any())).thenReturn(true);
        when(sendGridTokenVerifier.extractInbox(any())).thenReturn("zoo");

        String content = "" +
                "{" +
                "  \"personalizations\": [{" +
                "      \"bcc\": [" +
                "        {\"email\": \"bcc@example.com\", \"name\": \"BeeCC\"}, " +
                "        {\"email\": \"bcc2@example.com\"}" +
                "      ] " +
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


        verify(sendGridEmailFactory).getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", null),
                        asList(
                                new PersonalizationForm(
                                        null,
                                        null,
                                        asList(
                                                new AddressForm("bcc@example.com", "BeeCC"),
                                                new AddressForm("bcc2@example.com", null)
                                        )
                                )
                        ),
                        asList(
                                new ContentForm("text/plain", "Content content content!")
                        )
                ),
                "zoo"
        );

        verify(emailWebSocketHandler).broadcastNewEmailMessage(savedRecord);
    }

    @Test
    public void testMailSend_DisplayNames() throws Exception {
        EmailRecord generatedRecord = mock(EmailRecord.class);
        when(sendGridEmailFactory.getEmailsFromRequest(any(), any())).thenReturn(asList(generatedRecord));

        EmailRecord savedRecord = mock(EmailRecord.class);
        when(emailRepository.save(generatedRecord)).thenReturn(savedRecord);

        when(sendGridTokenVerifier.verify(any())).thenReturn(true);
        when(sendGridTokenVerifier.extractInbox(any())).thenReturn("zoo");

        String content = "" +
                "{" +
                "  \"personalizations\": [{" +
                "    \"to\": [{" +
                "      \"email\": \"to1@example.com\", " +
                "      \"name\": \"To Name\"" +
                "    }, {" +
                "      \"email\": \"to2@example.com\"" +
                "    }" +
                "  ]}, {" +
                "    \"to\": [{" +
                "      \"email\": \"to3@example.com\", " +
                "      \"name\": \"Another\"" +
                "    }]" +
                "  }]," +
                "  \"subject\": \"My subjecT\"," +
                "  \"from\": {" +
                "    \"email\": \"sender@example.com\", " +
                "    \"name\": \"The Sender\"" +
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


        verify(sendGridEmailFactory).getEmailsFromRequest(
                new MailSendForm(
                        "My subjecT",
                        new AddressForm("sender@example.com", "The Sender"),
                        asList(
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to1@example.com", "To Name"),
                                                new AddressForm("to2@example.com", null)
                                        ),
                                        null,
                                        null
                                ),
                                new PersonalizationForm(
                                        asList(
                                                new AddressForm("to3@example.com", "Another")
                                        ),
                                        null,
                                        null
                                )
                        ),
                        asList(
                                new ContentForm("text/plain", "Content content content!")
                        )
                ),
                "zoo"
        );

        verify(emailWebSocketHandler).broadcastNewEmailMessage(savedRecord);
    }


    @Test
    public void testMailSend_MultipleEmails() throws Exception {
        EmailRecord generatedFirst = mock(EmailRecord.class);
        EmailRecord generatedSecond = mock(EmailRecord.class);
        when(sendGridEmailFactory.getEmailsFromRequest(any(), any()))
                .thenReturn(asList(generatedFirst, generatedSecond));

        EmailRecord savedFirst = mock(EmailRecord.class);
        when(emailRepository.save(generatedFirst)).thenReturn(savedFirst);

        EmailRecord savedSecond = mock(EmailRecord.class);
        when(emailRepository.save(generatedSecond)).thenReturn(savedSecond);

        when(sendGridTokenVerifier.verify(any())).thenReturn(true);
        when(sendGridTokenVerifier.extractInbox(any())).thenReturn("zoo");

        String content = "" +
                "{" +
                "  \"personalizations\": []," +
                "  \"subject\": \"\"," +
                "  \"from\": {" +
                "    \"email\": \"\"" +
                "  }," +
                "  \"content\": [{" +
                "    \"type\": \"text/plain\"," +
                "    \"value\": \"\"" +
                "  }]" +
                "}";


        mockMvc.perform(
                post("/eapi/sendgrid/v3/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer foobar")
        );


        verify(emailRepository).save(generatedFirst);
        verify(emailRepository).save(generatedSecond);

        verify(emailWebSocketHandler).broadcastNewEmailMessage(savedFirst);
        verify(emailWebSocketHandler).broadcastNewEmailMessage(savedSecond);
    }

    @Test
    public void testMailSend_NoEmails() throws Exception {
        when(sendGridEmailFactory.getEmailsFromRequest(any(), any()))
                .thenReturn(emptyList());

        when(sendGridTokenVerifier.verify(any())).thenReturn(true);
        when(sendGridTokenVerifier.extractInbox(any())).thenReturn("zoo");

        String content = "" +
                "{" +
                "  \"personalizations\": []," +
                "  \"subject\": \"\"," +
                "  \"from\": {" +
                "    \"email\": \"\"" +
                "  }," +
                "  \"content\": [{" +
                "    \"type\": \"text/plain\"," +
                "    \"value\": \"\"" +
                "  }]" +
                "}";


        mockMvc.perform(
                post("/eapi/sendgrid/v3/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer foobar")
        );


        verifyZeroInteractions(emailRepository);
        verifyZeroInteractions(emailWebSocketHandler);
    }


    @Test
    public void testMailSend_InvalidToken() throws Exception {
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

        verifyZeroInteractions(sendGridEmailFactory);
        verifyZeroInteractions(emailRepository);
        verifyZeroInteractions(emailWebSocketHandler);

        verify(sendGridTokenVerifier, times(0)).extractInbox(any());
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

        verifyZeroInteractions(sendGridEmailFactory);
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

        verifyZeroInteractions(sendGridEmailFactory);
        verifyZeroInteractions(sendGridTokenVerifier);
        verifyZeroInteractions(emailRepository);
        verifyZeroInteractions(emailWebSocketHandler);
    }
}