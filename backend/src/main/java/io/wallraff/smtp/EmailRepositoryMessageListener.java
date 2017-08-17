package io.wallraff.smtp;


import io.wallraff.api.EmailWebSocketHandler;
import io.wallraff.data.EmailRecord;
import io.wallraff.data.EmailRepository;
import org.springframework.stereotype.Service;
import org.subethamail.smtp.helper.SimpleMessageListener;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class EmailRepositoryMessageListener implements SimpleMessageListener {

    private final EmailRepository emailRepository;
    private final EmailWebSocketHandler wsHandler;
    private final Session session;

    public EmailRepositoryMessageListener(EmailRepository emailRepository, EmailWebSocketHandler wsHandler) {
        this.emailRepository = emailRepository;
        this.wsHandler = wsHandler;
        this.session = Session.getDefaultInstance(new Properties());
    }

    @Override
    public boolean accept(String from, String recipient) {
        return true;
    }

    @Override
    public void deliver(String from, String recipient, InputStream data) throws IOException {
        try {
            MimeMessage message = new MimeMessage(session, data);

            EmailRecord result = emailRepository.save(new EmailRecord(
                    null,
                    message.getFrom()[0].toString(),
                    message.getAllRecipients()[0].toString(),
                    message.getSubject(),
                    message.getContent().toString()
            ));

            wsHandler.broadcastNewEmailMessage(result);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
