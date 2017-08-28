package io.wallraff.sendgrid;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class SendGridTokenVerifier {
    private final JWTVerifier verifier;

    public SendGridTokenVerifier(
            @Value("${jwtSecret}") String secret
    ) {
        if (secret == null || secret.equals("")) {
            throw new IllegalArgumentException("`jwtSecret` configuration must be set");
        }

        Algorithm algorithm;
        try {
            algorithm = Algorithm.HMAC256(secret);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("`jwtSecret` not supported", e);
        }

        verifier = JWT.require(algorithm)
                .withIssuer("Wallraff")
                .build();
    }

    public boolean verify(String token) {
        try {
            verifier.verify(token);

            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    public String extractInbox(String token) {
        DecodedJWT jwt = verifier.verify(token);

        return jwt.getClaim("inbox").asString();
    }
}
