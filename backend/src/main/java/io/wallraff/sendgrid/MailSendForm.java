package io.wallraff.sendgrid;

import java.util.List;

public class MailSendForm {
    private String subject;
    private AddressForm from;
    private List<PersonalizationForm> personalizations;
    private List<ContentForm> content;

    public MailSendForm() {
    }

    public String getSubject() {
        return subject;
    }

    public AddressForm getFrom() {
        return from;
    }

    public List<PersonalizationForm> getPersonalizations() {
        return personalizations;
    }

    public List<ContentForm> getContent() {
        return content;
    }
}
