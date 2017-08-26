package io.wallraff.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class EndpointControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private EndpointController controller;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testList() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/api/endpoints").contentType(MediaType.APPLICATION_JSON)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))

                .andExpect(jsonPath("$[0].id", equalTo("sendgrid")))
                .andExpect(jsonPath("$[0].name", equalTo("SendGrid")))
                .andExpect(jsonPath("$[0].originalHost", equalTo("https://api.sendgrid.com")))
                .andExpect(jsonPath("$[0].newHost", equalTo("https://wallraff.cfapps.io/eapi/sendgrid")))
                .andExpect(jsonPath("$[0].example", equalTo("POST https://wallraff.cfapps.io/eapi/sendgrid/v3/mail/send <data>")))
                .andExpect(jsonPath("$[0].*", hasSize(5)));
    }
}
