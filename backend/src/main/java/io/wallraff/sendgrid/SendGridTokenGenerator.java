package io.wallraff.sendgrid;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class SendGridTokenGenerator {
    private final Algorithm algorithm;

    public SendGridTokenGenerator(
            @Value("${jwtSecret}") String secret
    ) {
        if (secret == null || secret.equals("")) {
            throw new IllegalArgumentException("`jwtSecret` configuration must be set");
        }

        try {
            algorithm = Algorithm.HMAC256(secret);
        } catch (UnsupportedEncodingException exception) {
            throw new IllegalArgumentException("`jwtSecret` not supported");
        }
    }

    public String generateToken(String inbox) {
        try {
            return JWT.create()
                    .withIssuer("Wallraff")
                    .withClaim("inbox", inbox)
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }

        return null;
    }
}
