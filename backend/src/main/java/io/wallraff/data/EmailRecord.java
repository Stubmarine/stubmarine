package io.wallraff.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "email")
public class EmailRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "_from")
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String body;
    @JsonIgnore
    private String inbox;

    protected EmailRecord() {
    }

    public EmailRecord(Integer id, String from, String to, String cc, String bcc, String subject, String body, String inbox) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
        this.inbox = inbox;
    }

    public Integer getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getCc() {
        return cc;
    }

    public String getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getInbox() {
        return inbox;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailRecord that = (EmailRecord) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;
        if (cc != null ? !cc.equals(that.cc) : that.cc != null) return false;
        if (bcc != null ? !bcc.equals(that.bcc) : that.bcc != null) return false;
        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (body != null ? !body.equals(that.body) : that.body != null) return false;
        return inbox != null ? inbox.equals(that.inbox) : that.inbox == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (cc != null ? cc.hashCode() : 0);
        result = 31 * result + (bcc != null ? bcc.hashCode() : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (inbox != null ? inbox.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EmailRecord{" +
                "id=" + id +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", cc='" + cc + '\'' +
                ", bcc='" + bcc + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", inbox='" + inbox + '\'' +
                '}';
    }
}
