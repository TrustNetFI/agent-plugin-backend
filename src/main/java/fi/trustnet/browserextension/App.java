//https://github.com/ulymarins/chrome-native-messaging-java/blob/master/host-project/src/main/java/br/net/cartoriodigital/Application.java
package fi.trustnet.browserextension;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.trustnet.browserextension.errors.ErrorCode;
import fi.trustnet.browserextension.errors.ErrorMessage;
import fi.trustnet.browserextension.user.*;
import fi.trustnet.browserextension.responses.CreateAccountResponse;
import fi.trustnet.browserextension.responses.LoginResponse;
import fi.trustnet.browserextension.responses.LogoutResponse;
import fi.trustnet.browserextension.responses.Response;
import org.hyperledger.indy.sdk.LibIndy;

import java.io.*;
import java.util.LinkedHashMap;

import static fi.trustnet.browserextension.CommandCodes.*;
import static fi.trustnet.browserextension.Configuration.LIBINDY_LOCATION;
import static fi.trustnet.browserextension.Configuration.RUN_AS_EXTENSION;
import static fi.trustnet.browserextension.PayloadKeys.*;
import static fi.trustnet.browserextension.StatusCodes.CREATED;
import static fi.trustnet.browserextension.StatusCodes.ERROR;
import static fi.trustnet.browserextension.errors.ExceptionMessage.createErrorMessageFromException;

public class App 
{



    private static ObjectMapper objectMapper;

    public static void main( String[] args ) {


        UserStore.initUserDb();
        TokenStore.initTokenDb();

        objectMapper = new ObjectMapper();

        if (!LibIndy.isInitialized()) LibIndy.init(new File(LIBINDY_LOCATION));



        try {
            if (!RUN_AS_EXTENSION) {
                System.out.println("enter commands, ctrl-c to exit");
                BufferedReader br = null;
                BufferedWriter bw = null;
                InputStreamReader in= new InputStreamReader(System.in);
                OutputStreamWriter out = new OutputStreamWriter(System.out);
                br = new BufferedReader(in);
                bw = new BufferedWriter(out);
                while (true) {
                    String cmd = br.readLine();
                    String response = processInput(cmd);
                    bw.newLine();
                    bw.write(response);
                    bw.newLine();
                    bw.flush();
            }

            }
            else {
                while (true) {
                    byte[] msgLen = new byte[4];
                    System.in.read(msgLen);
                    int size = getInt(msgLen);
                    writeReqToFile("size " + size);
                    byte[] b = new byte[size];
                    System.in.read(b, 0, size);
                    writeReqToFile(new String(b, "UTF-8"));

                    String response = processInput(new String(b, "UTF-8"));

                    System.out.write(getBytes(response.length()));
                    System.out.write(response.getBytes("UTF-8"));
                    System.out.flush();
                }
            }

        } catch (IOException e) {
            if (RUN_AS_EXTENSION)
                Logger.writeToLog("Exception in msg reader loop" + e.getMessage());
            else
                e.printStackTrace();
        }
    }

    private static int getInt(byte[] bytes) {
        return (bytes[3] << 24) & 0xff000000 | (bytes[2] << 16) & 0x00ff0000
                | (bytes[1] << 8) & 0x0000ff00 | (bytes[0] << 0) & 0x000000ff;
    }

    private static byte[] getBytes(int length) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (length & 0xFF);
        bytes[1] = (byte) ((length >> 8) & 0xFF);
        bytes[2] = (byte) ((length >> 16) & 0xFF);
        bytes[3] = (byte) ((length >> 24) & 0xFF);
        return bytes;
    }

    private static void writeReqToFile(String req) {
        try {
            FileWriter fw = new FileWriter("log.txt", true);
            fw.write(req + "\n");
            fw.flush();
            fw.close();
        }
        catch (Exception e) {
        }
    }

    private static String getWalletName(Object params) {
        if (!(params instanceof LinkedHashMap)) {
            return "";
        }
        String tokenId = ((LinkedHashMap)params).get(TOKEN).toString();
        Token token = TokenStore.findTokenByTokenId(tokenId);
        if (token == null) {
            return "";
        }
        return UserStore.getWalletNameByUsername(token.getUsername());
    }

    private static String getDid(Object params) {
        if (!(params instanceof LinkedHashMap)) {
            return "";
        }
        return ((LinkedHashMap)params).get(DID).toString();
    }

    private static String getDataToSign(Object params) {
        if (!(params instanceof LinkedHashMap)) {
            return "";
        }
        return ((LinkedHashMap)params).get(DATA_TO_SIGN).toString();
    }


    private static String getMessage(Object params) {
        if (!(params instanceof LinkedHashMap)) {
            return "";
        }
        return ((LinkedHashMap)params).get(MESSAGE).toString();
    }


    private static String getSignature(Object params) {
        if (!(params instanceof LinkedHashMap)) {
            return "";
        }
        return ((LinkedHashMap)params).get(SIGNATURE).toString();
    }


    private static String getUsername(Object params) {
        if (!(params instanceof LinkedHashMap)) {
            return "";
        }
        return ((LinkedHashMap)params).get(USERNAME).toString();
    }


    private static String getPassword(Object params) {
        if (!(params instanceof LinkedHashMap)) {
            return "";
        }
        return ((LinkedHashMap)params).get(PASSWORD).toString();
    }


    private static String getToken(Object params) {
        if (!(params instanceof LinkedHashMap)) {
            return "";
        }
        return ((LinkedHashMap)params).getOrDefault(TOKEN, "").toString();
    }

    private static boolean isTokenValid(String tokenId) {
        if (TokenStore.findTokenByTokenId(tokenId) == null ){
            return false;
        }
        else {
            return true;
        }
    }

    private static boolean verifyParams(String opType, Object params) {
        if (!(params instanceof LinkedHashMap)) {
            return false;
        }
        switch (opType) {
            case CREATE_DID : {
                if (((LinkedHashMap)params).get(TOKEN) == null) {
                    return false;
                }
                else {
                    return true;
                }
            }

            case SIGN_DATA : {
                if (((LinkedHashMap)params).get(TOKEN) == null || ((LinkedHashMap)params).get(DID) == null
                        ||((LinkedHashMap)params).get(DATA_TO_SIGN) == null) {
                    return false;
                }
                else {
                    return true;
                }
            }
            case VERIFY_SIGNATURE : {
                if (((LinkedHashMap)params).get(TOKEN) == null || ((LinkedHashMap)params).get(DID) == null
                        ||((LinkedHashMap)params).get(SIGNATURE) == null || ((LinkedHashMap)params).get(MESSAGE) == null) {
                    return false;
                }
                else {
                    return true;
                }
            }
            case GET_ALL_DIDS : {
                if (((LinkedHashMap)params).get(TOKEN) == null) {
                    return false;
                }
                else {
                    return true;
                }
            }

            case LOGIN :
            case CREATE_ACCOUNT : {
                if (((LinkedHashMap)params).get(USERNAME) == null || ((LinkedHashMap)params).get(PASSWORD) == null ) {
                    return false;
                }
                else {
                    return true;
                }
            }

            case LOGOUT : {
                if (((LinkedHashMap)params).get(TOKEN) == null) {
                    return false;
                }
                else {
                    return true;
                }

            }

        }
        return false;
    }

    private static String processInput(String input) {

        try {
            Command cmd = objectMapper.readValue(input, Command.class);
            if (cmd.getOperation() == null)
                throw new Exception();
            Object params = cmd.getParams();
            String walletName;
            if (!cmd.getOperation().equals(LOGIN) && !cmd.getOperation().equals(CREATE_ACCOUNT)) {
                if (!verifyParams(CREATE_DID, params))  {
                    return createErrorResponse(706, "Missing token");
                }
                if (!isTokenValid(getToken(params))) {
                    return createErrorResponse(705, "Unauthorized");
                }
            }
            switch (cmd.getOperation()) {
                case CREATE_DID : {
                    if (verifyParams(CREATE_DID,params)) {
                        walletName = getWalletName(params);
                        try {

                            DidParams didParams = DidCommands.createDid(walletName, null, null);
                            Response response = new Response();
                            response.setStatus(CREATED);
                            response.setValue(didParams);
                            return objectMapper.writeValueAsString(response);
                        }
                        catch (Exception e) {
                            return createErrorMessageFromException(e);
                        }
                    }
                    else return createErrorResponse(700, "wrong parameters");
                }
                case SIGN_DATA : {
                    if (verifyParams(SIGN_DATA,params)) {
                        walletName = getWalletName(params);
                        String did = getDid(params);
                        String dataToSign = getDataToSign(params);
                        return Signature.Base64EncodedSignature(walletName,did, dataToSign);
                    }
                    else return createErrorResponse(700, "wrong parameters");
                }
                case VERIFY_SIGNATURE : {
                    if (verifyParams(VERIFY_SIGNATURE, params)) {
                        walletName = getWalletName(params);
                        String did = getDid(params);
                        String signature = getSignature(params);
                        String message = getMessage(params);
                        return Signature.VerifyBase64EncodedSignature(walletName, signature, message, did);
                    }
                    else return createErrorResponse(700, "wrong parameters");
                }

                case GET_ALL_DIDS : {
                    if (verifyParams(GET_ALL_DIDS, params)) {
                        walletName = getWalletName(params);
                        return DidCommands.getAllDids(walletName);
                    }
                }

                case CREATE_ACCOUNT : {
                    if (verifyParams(CREATE_ACCOUNT, params)) {
                        String username = getUsername(params);
                        String password = getPassword(params);
                        if (UserStore.findUserByUsername(username) != null) {
                           return createErrorResponse(703, "user with name " + username + " already exitss");
                        }
                        UserAccount account = new UserAccount();
                        account.setUsername(username);
                        //userstore hashes pwd before saving
                        account.setPassword(password);
                        account.setWalletname(java.util.UUID.randomUUID().toString());
                        UserStore.storeNewUser(account);

                        try {
                            Agent.initializeAgent(account.getWalletname(), account.getUsername());

                            CreateAccountResponse createAccountResponse = new CreateAccountResponse();
                            createAccountResponse.setUsername(username);
                            createAccountResponse.setWalletname(account.getWalletname());
                            Response resp = new Response(StatusCodes.SUCCESS, createAccountResponse);
                            return getJsonValue(resp);
                        }
                        catch (Exception e){
                            UserStore.deleteUser(account.getUsername());
                            return createErrorResponse(800, "Could not initialize agent");
                        }
                    }
                }

                case LOGIN : {
                    if (verifyParams(LOGIN, params)) {
                        String username = getUsername(params);
                        String password = getPassword(params);
                        if (UserStore.login(username, password)) {
                            Token token = new Token();
                            token.setToken(java.util.UUID.randomUUID().toString());
                            token.setUsername(username);
                            TokenStore.storeNewToken(token);
                            LoginResponse loginResponse = new LoginResponse();
                            loginResponse.setToken(token.getToken());
                            Response resp = new Response(StatusCodes.SUCCESS, loginResponse);
                            return getJsonValue(resp);
                        }
                        return createErrorResponse(704, "wrong username or password");
                    }
                }

                case LOGOUT : {
                    if (verifyParams(LOGOUT, params)) {
                        String tokenId = getToken(params);
                            if (TokenStore.findTokenByTokenId(tokenId) != null) {
                                TokenStore.removeToken(tokenId);
                                LogoutResponse logoutResponse = new LogoutResponse();
                                logoutResponse.setLoggedout(true);
                                Response resp = new Response(StatusCodes.SUCCESS, logoutResponse);
                                return getJsonValue(resp);
                            }
                            else {
                                return createErrorResponse(705, "Incorrect token");
                            }
                    }
                }

                default : {
                    return createErrorResponse(701, "unknown operation");
                }
            }


        }
        catch (Exception e) {
            //e.printStackTrace();
            return createErrorResponse(700, "malformed request");
        }

    }

    private static String createErrorResponse(int code, String description) {

        ErrorMessage error = new ErrorMessage(ErrorCode.valueOf(code).toString(), description);
        Response response = new Response(ERROR, error);
        return getJsonValue(response);
    }


    public static String getJsonValue(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(obj);
        }
        catch (Exception e) {
            return "{\"status\":\"ERROR\",\"value\":{\"errorcode\":\"INTERNAL_ERROR\",\"description\":\"ObjectMapper exception\"}}";
        }
    }



}
