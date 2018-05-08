package fi.trustnet.browserextension.responses;

public class WalletResult {
    private String walletname;
    private String operation;

    public WalletResult() {
    }

    public String getWalletname() {
        return walletname;
    }

    public void setWalletname(String walletname) {
        this.walletname = walletname;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
