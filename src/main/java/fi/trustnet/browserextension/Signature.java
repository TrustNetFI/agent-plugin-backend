package fi.trustnet.browserextension;

import fi.trustnet.browserextension.responses.Response;
import fi.trustnet.browserextension.responses.SignatureResult;
import fi.trustnet.browserextension.responses.SignatureVerificationResult;
import fi.trustnet.browserextension.user.Logger;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;

import java.util.Base64;


import static fi.trustnet.browserextension.App.getJsonValue;
import static fi.trustnet.browserextension.Configuration.NETWORK_NAME;
import static fi.trustnet.browserextension.Configuration.RUN_AS_EXTENSION;
import static fi.trustnet.browserextension.errors.ExceptionMessage.createErrorMessageFromException;

public class Signature {

    public static String Base64EncodedSignature(String walletName, String did, String dataToSign) {
        Pool pool =null;
        Wallet wallet = null;
        try {
            pool = Pool.openPoolLedger(NETWORK_NAME, "{}").get();
            wallet = Wallet.openWallet(walletName, null, null).get();
            String key = Did.keyForLocalDid(wallet, did).get();
            byte[] signed = Crypto.cryptoSign(wallet, key,dataToSign.getBytes()).get();
            byte[] encoded = Base64.getEncoder().encode(signed);
            wallet.closeWallet().get();
            wallet = null;
            pool.closePoolLedger().get();
            pool = null;


            SignatureResult signatureResult = new SignatureResult();
            signatureResult.setDid(did);
            signatureResult.setVerkey(key);
            signatureResult.setSignature(new String(encoded));
            signatureResult.setDataToSign(dataToSign);
            signatureResult.setWallet(walletName);
            Response response = new Response(StatusCodes.SUCCESS,signatureResult);

            return getJsonValue(response);
        }
        catch (Exception e) {
            try{
                if (RUN_AS_EXTENSION)
                    Logger.writeToLog("Base64EncodedSignature" + e.getMessage());
                else
                    System.out.println(e.getMessage());

                if (pool != null)
                    pool.closePoolLedger().get();
                if (wallet != null)
                    wallet.closeWallet().get();
                return createErrorMessageFromException(e);
            }
            catch (Exception ex) {
                if (RUN_AS_EXTENSION)
                    Logger.writeToLog("Base64EncodedSignature" + ex.getMessage());
                else
                    System.out.println(ex.getMessage());
                return createErrorMessageFromException(ex);
            }
        }
    }
    public static String VerifyBase64EncodedSignature(String walletName, String signature, String message, String did) {
        Pool pool = null;
        Wallet wallet = null;
        try {
            pool = Pool.openPoolLedger(NETWORK_NAME, "{}").get();
            wallet = Wallet.openWallet(walletName, null, null).get();
            String key = Did.keyForLocalDid(wallet, did).get();
            Boolean verified = Crypto.cryptoVerify(key, message.getBytes(), Base64.getDecoder().decode(signature)).get();
            wallet.closeWallet().get();
            pool.closePoolLedger().get();

            SignatureVerificationResult signatureVerificationResult = new SignatureVerificationResult();
            signatureVerificationResult.setDid(did);
            signatureVerificationResult.setMessage(message);
            signatureVerificationResult.setVerified(verified);
            signatureVerificationResult.setSignature(signature);


            if (verified) {
                Response response = new Response(StatusCodes.SUCCESS, signatureVerificationResult);
                return getJsonValue(response);
            }
            else {
                Response response = new Response(StatusCodes.FAIL, signatureVerificationResult);
                return getJsonValue(response);
            }
        }
        catch (Exception e){
            if (RUN_AS_EXTENSION)
                Logger.writeToLog("VerifyBase64EncodedSignature" + e.getMessage());
            else
                System.out.println(e.getMessage());
            try {
                if (wallet != null)
                    wallet.closeWallet().get();
                if (pool != null)
                    pool.closePoolLedger().get();
            }
            catch (Exception ex) {
                if (RUN_AS_EXTENSION)
                    Logger.writeToLog("Base64EncodedSignature" + ex.getMessage());
                else
                    System.out.println(ex.getMessage());

                return createErrorMessageFromException(ex);
            }
            return createErrorMessageFromException(e);
        }
    }
}
