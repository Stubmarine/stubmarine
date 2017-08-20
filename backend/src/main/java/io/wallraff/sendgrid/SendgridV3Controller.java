package io.wallraff.sendgrid;

import io.wallraff.api.EmailWebSocketHandler;
import io.wallraff.data.EmailRecord;
import io.wallraff.data.EmailRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SendgridV3Controller {
    private final EmailRepository emailRepository;
    private final EmailWebSocketHandler emailWebSocketHandler;

    public SendgridV3Controller(
            EmailRepository emailRepository,
            EmailWebSocketHandler emailWebSocketHandler
    ) {
        this.emailRepository = emailRepository;
        this.emailWebSocketHandler = emailWebSocketHandler;
    }

    @RequestMapping(path = "/eapi/sendgrid/v3/mail/send")
    public ResponseEntity mailSend(@RequestBody MailSendForm form) {
        EmailRecord newEmail = emailRepository.save(new EmailRecord(
                null,
                form.getFrom().getEmail(),
                form.getPersonalizations().stream()
                        .flatMap(p -> p.getTo().stream())
                        .map(AddressForm::getEmail)
                        .reduce("", (s, s2) -> s + s2),
                form.getSubject(),
                form.getContent().stream()
                        .filter(c -> c.getType().equals("text/plain"))
                        .findFirst()
                        .map(ContentForm::getValue)
                        .orElse("")
        ));

        try {
            emailWebSocketHandler.broadcastNewEmailMessage(newEmail);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
    }
}
