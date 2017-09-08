package io.wallraff.sendgrid;

import java.util.List;

public class PersonalizationForm {
    private List<AddressForm> to;
    private List<AddressForm> cc;

    public PersonalizationForm() {
    }

    public List<AddressForm> getTo() {
        return to;
    }

    public List<AddressForm> getCc() {
        return cc;
    }
}
