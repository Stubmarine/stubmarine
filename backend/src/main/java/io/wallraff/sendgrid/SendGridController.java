package io.wallraff.sendgrid;

import io.wallraff.api.EmailWebSocketHandler;
import io.wallraff.data.EmailRecord;
import io.wallraff.data.EmailRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SendGridController {
    private final EmailRepository emailRepository;
    private final EmailWebSocketHandler emailWebSocketHandler;
    private final SendGridTokenVerifier sendGridTokenVerifier;
    private final SendGridEmailFactory sendGridEmailFactory;

    public SendGridController(
            EmailRepository emailRepository,
            EmailWebSocketHandler emailWebSocketHandler,
            SendGridTokenVerifier sendGridTokenVerifier,
            SendGridEmailFactory sendGridEmailFactory
    ) {
        this.emailRepository = emailRepository;
        this.emailWebSocketHandler = emailWebSocketHandler;
        this.sendGridTokenVerifier = sendGridTokenVerifier;
        this.sendGridEmailFactory = sendGridEmailFactory;
    }

    @RequestMapping(path = "/eapi/sendgrid/v3/mail/send")
    public ResponseEntity mailSend(
            @RequestBody MailSendForm form,
            @RequestHeader(required = false) String authorization
    ) {
        if (!checkAuthentication(authorization)) {
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }

        String inbox = sendGridTokenVerifier.extractInbox(authorization.substring(7));
        EmailRecord unsaved = sendGridEmailFactory.getEmailFromRequest(form, inbox);
        EmailRecord saved = emailRepository.save(unsaved);

        try {
            emailWebSocketHandler.broadcastNewEmailMessage(saved);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
    }

    private boolean checkAuthentication(@RequestHeader String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return false;
        }

        String token = authorization.substring(7);

        return sendGridTokenVerifier.verify(token);
    }
}
