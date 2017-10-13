package io.stubmarine.sendgrid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class SendGridTokenVerifierTest {

    private SendGridTokenVerifier verifier;

    @Before
    public void setUp() throws Exception {
        verifier = new SendGridTokenVerifier("secret");
    }

    @Test
    public void testVerify_ValidToken() throws Exception {
        String validToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJXYWxscmFmZiJ9.1bkRUKfYKI9VujwlDwVxfeng1VYKZrOTQiU-0xKfmUY";
        assertTrue(verifier.verify(validToken));
    }

    @Test
    public void testVerify_InvalidToken() throws Exception {
        assertFalse(verifier.verify("junk-token"));
    }

    @Test
    public void testVerify_DifferentIssuer() throws Exception {
        String bazIssuedToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJCYXoifQ.7AtT3CAb-LMVpMrrjFyIZVYAC5y_dwlNpdnyXyY1nG4";
        assertFalse(verifier.verify(bazIssuedToken));
    }

    @Test
    public void testExtractInbox() throws Exception {
        String inboxClaimOfPuppyToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJXYWxscmFmZiIsImlhdCI6MTUwNDA0NzE1MiwiZXhwIjpudWxsLCJhdWQiOiIiLCJzdWIiOiIiLCJpbmJveCI6InB1cHB5In0.OcRhAfCbYM9VQ_-obCdsF-n3j_2hWq7V5d1VXRO36zk";
        assertEquals(verifier.extractInbox(inboxClaimOfPuppyToken), "puppy");
    }
}