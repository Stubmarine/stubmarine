package io.stubmarine.wallraff.sendgrid;

import java.util.List;

public class MailSendForm {
    private String subject;
    private AddressForm from;
    private List<PersonalizationForm> personalizations;
    private List<ContentForm> content;

    public MailSendForm() {
    }

    protected MailSendForm(String subject, AddressForm from, List<PersonalizationForm> personalizations, List<ContentForm> content) {
        this.subject = subject;
        this.from = from;
        this.personalizations = personalizations;
        this.content = content;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MailSendForm that = (MailSendForm) o;

        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (personalizations != null ? !personalizations.equals(that.personalizations) : that.personalizations != null)
            return false;
        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (personalizations != null ? personalizations.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MailSendForm{" +
                "subject='" + subject + '\'' +
                ", from=" + from +
                ", personalizations=" + personalizations +
                ", content=" + content +
                '}';
    }
}
