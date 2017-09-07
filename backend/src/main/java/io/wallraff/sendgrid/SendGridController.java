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

    public SendGridController(
            EmailRepository emailRepository,
            EmailWebSocketHandler emailWebSocketHandler,
            SendGridTokenVerifier sendGridTokenVerifier
    ) {
        this.emailRepository = emailRepository;
        this.emailWebSocketHandler = emailWebSocketHandler;
        this.sendGridTokenVerifier = sendGridTokenVerifier;
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

        EmailRecord newEmail = emailRepository.save(new EmailRecord(
                null,
                toRecipient(form.getFrom()),
                form.getPersonalizations().stream()
                        .flatMap(p -> p.getTo().stream())
                        .map(SendGridController::toRecipient)
                        .reduce("", (s, s2) -> s + (s.equals("") ? "" : ", ") + s2),
                form.getSubject(),
                form.getContent().stream()
                        .filter(c -> c.getType().equals("text/plain"))
                        .findFirst()
                        .map(ContentForm::getValue)
                        .orElse(""),
                inbox
        ));

        try {
            emailWebSocketHandler.broadcastNewEmailMessage(newEmail);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
    }

    private static String toRecipient(AddressForm address) {
        if (address.getName() != null) {
            return String.format("%s <%s>", address.getName(), address.getEmail());
        }

        return address.getEmail();
    }

    private boolean checkAuthentication(@RequestHeader String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return false;
        }

        String token = authorization.substring(7);

        return sendGridTokenVerifier.verify(token);
    }
}
