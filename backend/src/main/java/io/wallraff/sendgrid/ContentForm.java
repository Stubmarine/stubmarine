package io.wallraff.sendgrid;

public class ContentForm {
    private String type;
    private String value;

    public ContentForm() {
    }

    protected ContentForm(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentForm that = (ContentForm) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContentForm{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
