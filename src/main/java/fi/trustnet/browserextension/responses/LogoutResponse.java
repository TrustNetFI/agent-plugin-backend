package fi.trustnet.browserextension.responses;

public class LogoutResponse {
    public LogoutResponse() {
    }

    private boolean loggedout;

    public boolean isLoggedout() {
        return loggedout;
    }

    public void setLoggedout(boolean loggedout) {
        this.loggedout = loggedout;
    }
}
