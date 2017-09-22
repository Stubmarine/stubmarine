package io.stubmarine.wallraff.sendgrid;

public class AddressForm {
    private String email;
    private String name;

    public AddressForm() {
    }

    protected AddressForm(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressForm that = (AddressForm) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AddressForm{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
