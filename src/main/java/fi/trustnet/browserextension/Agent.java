package fi.trustnet.browserextension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.trustnet.browserextension.responses.CreateAccountResponse;
import fi.trustnet.browserextension.responses.Response;
import fi.trustnet.browserextension.user.Logger;
import org.hyperledger.indy.sdk.wallet.Wallet;


import static fi.trustnet.browserextension.Configuration.NETWORK_NAME;
import static fi.trustnet.browserextension.Configuration.RUN_AS_EXTENSION;

public class Agent {
    private static final String FRIENDLY_NAME ="friendlyName";

    public static String initializeAgent(String walletName, String username) throws Exception {

        try {
            Wallet.createWallet(NETWORK_NAME, walletName, "default", null, null).get();

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode didMetadata = objectMapper.createObjectNode();
            didMetadata.put(FRIENDLY_NAME, username + "@EdgeAgent");

            DidCommands.createDid(walletName, didMetadata.toString(), null );

        }
        catch (Exception e) {

            if (RUN_AS_EXTENSION)
                Logger.writeToLog("initializeAgent " + e.getMessage());
            else
                System.out.println(e.getMessage());
            throw e;
        }

        return walletName;
    }
}
