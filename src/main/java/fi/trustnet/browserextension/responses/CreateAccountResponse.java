package fi.trustnet.browserextension.responses;

public class CreateAccountResponse {
    private String username;
    private String walletname;

    public CreateAccountResponse() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWalletname() {
        return walletname;
    }

    public void setWalletname(String walletname) {
        this.walletname = walletname;
    }
}
