package io.stubmarine.api;

import io.stubmarine.sendgrid.SendGridTokenGenerator;
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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class EndpointControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private EndpointController controller;

    @Mock
    private SendGridTokenGenerator sendGridTokenGenerator;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testList() throws Exception {
        when(sendGridTokenGenerator.generateToken(any())).thenReturn("SOME_TOKEN");

        ResultActions result = mockMvc.perform(
                get("/api/inbox/larry/endpoints").contentType(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))

                .andExpect(jsonPath("$[0].id", equalTo("sendgrid")))
                .andExpect(jsonPath("$[0].name", equalTo("SendGrid")))
                .andExpect(jsonPath("$[0].originalHost", equalTo("https://api.sendgrid.com")))
                .andExpect(jsonPath("$[0].newHost", equalTo("https://stubmarine.cfapps.io/eapi/sendgrid")))
                .andExpect(jsonPath("$[0].newToken", equalTo("SOME_TOKEN")))
                .andExpect(jsonPath("$[0].example", equalTo("POST https://stubmarine.cfapps.io/eapi/sendgrid/v3/mail/send <data>")))
                .andExpect(jsonPath("$[0].*", hasSize(6)));

        verify(sendGridTokenGenerator).generateToken("larry");
    }
}
