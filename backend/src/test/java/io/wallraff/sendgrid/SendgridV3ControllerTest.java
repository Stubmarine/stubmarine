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
public class SendgridV3ControllerTest {

    @InjectMocks
    private SendgridV3Controller controller;

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private EmailWebSocketHandler emailWebSocketHandler;

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
                "sender@sendgrid.com",
                "recip@sendgrid.com",
                "My subjecT",
                "Content content content!"
        ))).thenReturn(savedRecord);

        String content = "" +
                "{" +
                "  \"personalizations\": [{" +
                "    \"to\": [{" +
                "      \"email\": \"recip@sendgrid.com\"" +
                "    }]" +
                "  }]," +
                "  \"subject\": \"My subjecT\"," +
                "  \"from\": {" +
                "    \"email\": \"sender@sendgrid.com\"" +
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
                .andExpect(status().isAccepted())
                .andExpect(content().string(""));

        verify(emailWebSocketHandler).broadcastNewEmailMessage(savedRecord);
    }
}