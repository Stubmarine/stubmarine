package io.stubmarine.wallraff.sendgrid;

import io.stubmarine.wallraff.data.EmailRecord;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class SendGridEmailFactory {
    public EmailRecord getEmailFromRequest(MailSendForm form, String inbox) {
        return new EmailRecord(
                null,
                toRecipient(form.getFrom()),
                form.getPersonalizations().stream()
                        .flatMap(p -> emptyOrList(p.getTo()).stream())
                        .map(SendGridEmailFactory::toRecipient)
                        .reduce("", (s, s2) -> s + (s.equals("") ? "" : ", ") + s2),
                form.getPersonalizations().stream()
                        .flatMap(p -> emptyOrList(p.getCc()).stream())
                        .map(SendGridEmailFactory::toRecipient)
                        .reduce("", (s, s2) -> s + (s.equals("") ? "" : ", ") + s2),
                form.getPersonalizations().stream()
                        .flatMap(p -> emptyOrList(p.getBcc()).stream())
                        .map(SendGridEmailFactory::toRecipient)
                        .reduce("", (s, s2) -> s + (s.equals("") ? "" : ", ") + s2),
                form.getSubject(),
                form.getContent().stream()
                        .filter(c -> c.getType().equals("text/plain"))
                        .findFirst()
                        .map(ContentForm::getValue)
                        .orElse(""),
                inbox
        );
    }

    private static <T> List<T> emptyOrList(List<T> items) {
        if (items == null) {
            return emptyList();
        }

        return items;
    }

    private static String toRecipient(AddressForm address) {
        if (address.getName() != null) {
            return String.format("%s <%s>", address.getName(), address.getEmail());
        }

        return address.getEmail();
    }
}
