package fi.trustnet.browserextension;

import fi.trustnet.browserextension.user.Logger;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidJSONParameters;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;


import static fi.trustnet.browserextension.Configuration.NETWORK_NAME;
import static fi.trustnet.browserextension.Configuration.RUN_AS_EXTENSION;

public class DidCommands {


    public static DidParams createDid(String walletName, String didMetadata, String serviceUrl) throws Exception {
        Pool pool = null;
        Wallet wallet = null;
        try {
            pool = Pool.openPoolLedger(NETWORK_NAME, "{}").get();
            wallet = Wallet.openWallet(walletName, null, null).get();

            // USE GUID as seed, should be replaced with real PRNG;
            String seed = java.util.UUID.randomUUID().toString();
            seed = seed.replaceAll("-", "");


            DidJSONParameters.CreateAndStoreMyDidJSONParameter createAndStoreMyDidJSONParameter = new DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, seed , null, null);
            DidResults.CreateAndStoreMyDidResult createAndStoreMyDidResult = Did.createAndStoreMyDid(wallet, createAndStoreMyDidJSONParameter.toJson()).get();



            String did = createAndStoreMyDidResult.getDid();
            String verkey = createAndStoreMyDidResult.getVerkey();

            if (didMetadata != null) {
                Did.setDidMetadata(wallet, did, didMetadata);
            }
            if (serviceUrl != null) {
                Did.setEndpointForDid(wallet, did, serviceUrl, null);
            }

            DidParams didParams = new DidParams();
            didParams.setDid(did);
            didParams.setVerkey(verkey);
            didParams.setUrl(serviceUrl);
            didParams.setDidMetadata(didMetadata);

            wallet.closeWallet().get();
            wallet = null;
            pool.closePoolLedger().get();
            pool = null;

            return didParams;
        }
        catch (Exception e) {
            try {
                if (RUN_AS_EXTENSION)
                    Logger.writeToLog("createDid" + e.getMessage());
                else
                    System.out.println(e.getMessage());

                if (wallet != null)
                    wallet.closeWallet().get();
                if (pool != null)
                    pool.closePoolLedger().get();
                throw e;

            }
            catch (Exception ex) {
                if (RUN_AS_EXTENSION)
                    Logger.writeToLog("createDid" + ex.getMessage());
                else
                    System.out.println(ex.getMessage());

                throw ex;
            }
        }
    }

    public static String getAllDids(String walletName) {
        WalletDbReader walletDbReader = new WalletDbReader();
        return walletDbReader.getAllDids(walletName);
    }

}
