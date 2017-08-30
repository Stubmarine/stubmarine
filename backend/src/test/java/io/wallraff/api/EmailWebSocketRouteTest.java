package io.wallraff.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class EmailWebSocketRouteTest {

    @InjectMocks
    private EmailWebSocketRoute route;

    @Test
    public void testGetEndpointMask() throws Exception {
        assertThat(route.getPath(), equalTo("/wsapi/inbox/{inboxName}/emails"));
    }

    @Test
    public void testExtractInboxName() throws Exception {
        String result = route.extractInboxName(new URI(null, null, "/wsapi/inbox/foobar/emails", null));

        assertThat(result, equalTo("foobar"));
    }

    @Test(expected = IllegalStateException.class)
    public void testExtractInboxName_emptyString() throws Exception {
        route.extractInboxName(new URI(null, null, "/wsapi/inbox//emails", null));
    }

    @Test(expected = IllegalStateException.class)
    public void testExtractInboxName_differentPath() throws Exception {
        route.extractInboxName(new URI(null, null, "/hello/world", null));
    }
}