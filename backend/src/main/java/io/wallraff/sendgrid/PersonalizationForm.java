package io.wallraff.sendgrid;

import java.util.List;

public class PersonalizationForm {
    private List<AddressForm> to;
    private List<AddressForm> cc;
    private List<AddressForm> bcc;

    public PersonalizationForm() {
    }

    protected PersonalizationForm(List<AddressForm> to, List<AddressForm> cc, List<AddressForm> bcc) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
    }

    public List<AddressForm> getTo() {
        return to;
    }

    public List<AddressForm> getCc() {
        return cc;
    }

    public List<AddressForm> getBcc() {
        return bcc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonalizationForm that = (PersonalizationForm) o;

        if (to != null ? !to.equals(that.to) : that.to != null) return false;
        if (cc != null ? !cc.equals(that.cc) : that.cc != null) return false;
        return bcc != null ? bcc.equals(that.bcc) : that.bcc == null;
    }

    @Override
    public int hashCode() {
        int result = to != null ? to.hashCode() : 0;
        result = 31 * result + (cc != null ? cc.hashCode() : 0);
        result = 31 * result + (bcc != null ? bcc.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersonalizationForm{" +
                "to=" + to +
                ", cc=" + cc +
                ", bcc=" + bcc +
                '}';
    }
}
