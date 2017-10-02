package io.stubmarine.wallraff.sendgrid;

import io.stubmarine.wallraff.data.EmailRecord;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
public class SendGridEmailFactory {
    public Collection<EmailRecord> getEmailsFromRequest(MailSendForm form, String inbox) {
        return form.getPersonalizations().stream()
                .map(p -> new EmailRecord(
                        null,
                        toRecipient(form.getFrom()),
                        reduceToRecipients(p.getTo()),
                        reduceToRecipients(p.getCc()),
                        reduceToRecipients(p.getBcc()),
                        form.getSubject(),
                        form.getContent().stream()
                                .filter(c -> c.getType().equals("text/plain"))
                                .findFirst()
                                .map(ContentForm::getValue)
                                .orElse(""),
                        inbox
                ))
                .collect(Collectors.toList());
    }

    private static String reduceToRecipients(List<AddressForm> addresses) {
        return emptyOrList(addresses)
                .stream()
                .map(SendGridEmailFactory::toRecipient)
                .reduce("", (s, s2) -> s + (s.equals("") ? "" : ", ") + s2);
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
