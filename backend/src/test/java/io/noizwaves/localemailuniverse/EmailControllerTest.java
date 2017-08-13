package io.noizwaves.localemailuniverse;

import io.noizwaves.localemailuniverse.data.EmailRecord;
import io.noizwaves.localemailuniverse.data.EmailRepository;
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
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class EmailControllerTest {
    private MockMvc mockMvc;

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private EmailController controller;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testList() throws Exception {
        when(emailRepository.findAll()).thenReturn(asList(
                new EmailRecord(1, "foo", "bar", "baz", "zar"),
                new EmailRecord(22, "foo2", "bar2", "baz2", "zar2")
        ));

        ResultActions resultActions = mockMvc.perform(get("/api/emails").contentType(MediaType.APPLICATION_JSON));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].*", hasSize(4)))
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].from", equalTo("foo")))
                .andExpect(jsonPath("$[0].to", equalTo("bar")))
                .andExpect(jsonPath("$[0].subject", equalTo("baz")))

                .andExpect(jsonPath("$[1].id", equalTo(22)));
    }
}