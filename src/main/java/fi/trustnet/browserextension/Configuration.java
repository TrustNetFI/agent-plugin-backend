package fi.trustnet.browserextension;

public class Configuration {
    public static final String WALLET_BASEPATH = "jdbc:sqlite:/home/samuli/.indy_client/wallet/";
    public static final String USER_DB_FILE = "/home/samuli/.indy_client/users.db";
    public static final String TOKEN_DB_FILE = "/home/samuli/.indy_client/tokens.db";
    public static final String LIBINDY_LOCATION = "./lib/libindy.so";
    //set to true before creating jar for browser extension
    public static final boolean RUN_AS_EXTENSION = false;

    public static final String NETWORK_NAME = "default_pool";
}
