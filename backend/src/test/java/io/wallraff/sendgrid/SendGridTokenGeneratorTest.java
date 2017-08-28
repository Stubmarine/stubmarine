package io.wallraff.sendgrid;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class SendGridTokenGeneratorTest {

    private SendGridTokenGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new SendGridTokenGenerator("SECRETSHHH");
    }

    @Test
    public void testGenerateToken_Decodes() throws Exception {
        String token = generator.generateToken();

        DecodedJWT jwt = JWT.decode(token);

        assertNotNull(jwt);
    }

    @Test
    public void testGenerateToken_Verifies() throws Exception {
        String token = generator.generateToken();

        DecodedJWT jwt = JWT.require(Algorithm.HMAC256("SECRETSHHH"))
                .withIssuer("Wallraff")
                .build()
                .verify(token);

        assertNotNull(jwt);
    }
}