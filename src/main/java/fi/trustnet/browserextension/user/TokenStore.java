package fi.trustnet.browserextension.user;

import java.sql.*;

import static fi.trustnet.browserextension.Configuration.TOKEN_DB_FILE;

public class TokenStore {
    private static final String TABLENAME = "tokens";

    private static Connection connect() {

        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + TOKEN_DB_FILE;

            try {
                conn = DriverManager.getConnection(url);
            } catch (SQLException e) {
                //System.out.println(e.getMessage());
            }
        } catch (Exception e) {

        }
        return conn;
    }

    private static void createTables() {

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (\n"
                + "	token text PRIMARY KEY,\n"
                + "	username text NOT NULL\n"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            Logger.writeToLog(e.getMessage());
        }
    }




    public static void initTokenDb() {
        createTables();
    }

    public static void storeNewToken(Token token) {
        String sql = "INSERT INTO " + TABLENAME +" (token,username) VALUES(?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token.getToken());
            pstmt.setString(2, token.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.writeToLog(e.getMessage());
        }
    }

    public static Token findTokenByTokenId(String tokenId) {
        String sql = "SELECT * FROM " + TABLENAME + " WHERE token=\"" + tokenId +"\"";
        Token token = null;
        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            if(rs.next()) {
                token = new Token();
                token.setToken(rs.getString("token"));
                token.setUsername(rs.getString("username"));
            }

        } catch (SQLException e) {
            Logger.writeToLog(e.getMessage());
        }
        return token;
    }

    public static void removeToken(String tokenId) {
        String sql = "DELETE FROM " + TABLENAME + " WHERE token=\"" +tokenId + "\"";
        try (Connection conn = connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.executeUpdate();
        }

         catch (SQLException e) {
             Logger.writeToLog(e.getMessage());
        }
    }


}

