package io.wallraff.sendgrid;

import java.util.List;

public class PersonalizationForm {
    private List<AddressForm> to;

    public PersonalizationForm() {
    }

    public List<AddressForm> getTo() {
        return to;
    }
}
