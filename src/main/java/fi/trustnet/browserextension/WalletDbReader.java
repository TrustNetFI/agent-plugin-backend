package fi.trustnet.browserextension;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.trustnet.browserextension.responses.Response;
import fi.trustnet.browserextension.user.Logger;

import java.sql.*;

import static fi.trustnet.browserextension.App.getJsonValue;
import static fi.trustnet.browserextension.Configuration.RUN_AS_EXTENSION;
import static fi.trustnet.browserextension.Configuration.WALLET_BASEPATH;
import static fi.trustnet.browserextension.errors.ExceptionMessage.createErrorMessageFromException;

public class WalletDbReader {


    private Connection connect(String walletName) {
        // SQLite connection string
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = WALLET_BASEPATH + walletName + "/sqlite.db";

            try {
                conn = DriverManager.getConnection(url);
            } catch (SQLException e) {
                if (RUN_AS_EXTENSION)
                    Logger.writeToLog("WalletDbReader connect" + e.getMessage());
                else System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            if (RUN_AS_EXTENSION)
                Logger.writeToLog("WalletDbReader connect" + e.getMessage());
            else System.out.println(e.getMessage());
        }
        return conn;
    }


    public String getAllDids(String walletName){
        String sql = "SELECT * FROM  wallet WHERE KEY LIKE \"my%\"";
        ObjectMapper objectMapper = new ObjectMapper();
        try (Connection conn = this.connect(walletName);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            DidList didList = new DidList();
            // loop through the result set
            while (rs.next()) {
                DidParams didParams = objectMapper.readValue(rs.getString("value"), DidParams.class);
                didList.getDids().add(didParams);
            }
            Response response = new Response(StatusCodes.SUCCESS,didList);

            return getJsonValue(response);
        } catch (Exception e) {
            if (RUN_AS_EXTENSION)
                Logger.writeToLog("WalletDbReader getAllDids" + e.getMessage());
            else System.out.println(e.getMessage());

            return createErrorMessageFromException(e);
        }
    }
}