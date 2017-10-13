package io.stubmarine.api;


import io.stubmarine.data.EmailRecord;

public class EmailListItem {
    private final EmailRecord record;

    public EmailListItem(EmailRecord record) {
        this.record = record;
    }

    public int getId() {
        return record.getId();
    }

    public String getFrom() {
        return record.getFrom();
    }

    public String getTo() {
        return record.getTo();
    }

    public String getSubject() {
        return record.getSubject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailListItem that = (EmailListItem) o;

        return record != null ? record.equals(that.record) : that.record == null;
    }

    @Override
    public int hashCode() {
        return record != null ? record.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "EmailListItem{" +
                "record=" + record +
                '}';
    }
}
