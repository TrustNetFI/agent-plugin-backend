package fi.trustnet.browserextension.errors;

public class ErrorMessage {
    private String errorcode;
    private String Description;

    public ErrorMessage(String errorcode, String description) {
        this.errorcode = errorcode;
        Description = description;
    }

    public ErrorMessage() {
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


}
