package io.stubmarine.sendgrid;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class SendGridTokenGeneratorTest {

    private SendGridTokenGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new SendGridTokenGenerator("SECRETSHHH");
    }

    @Test
    public void testGenerateToken_Decodes() throws Exception {
        String token = generator.generateToken(null);

        DecodedJWT jwt = JWT.decode(token);

        assertNotNull(jwt);
    }

    @Test
    public void testGenerateToken_Verifies() throws Exception {
        String token = generator.generateToken(null);

        DecodedJWT jwt = JWT.require(Algorithm.HMAC256("SECRETSHHH"))
                .withIssuer("Wallraff")
                .build()
                .verify(token);

        assertNotNull(jwt);
    }

    @Test
    public void testGenerateToken_ContainsInboxClaim() throws Exception {
        String token = generator.generateToken("Bob");

        DecodedJWT jwt = JWT.require(Algorithm.HMAC256("SECRETSHHH"))
                .withIssuer("Wallraff")
                .build()
                .verify(token);

        assertThat(jwt.getClaim("inbox").asString(), equalTo("Bob"));
    }
}