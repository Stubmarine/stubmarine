package io.wallraff.sendgrid;

import java.util.List;

public class PersonalizationForm {
    private List<AddressForm> to;
    private List<AddressForm> cc;
    private List<AddressForm> bcc;

    public PersonalizationForm() {
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
}
