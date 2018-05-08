package fi.trustnet.browserextension.user;

import org.mindrot.jbcrypt.BCrypt;

import javax.jws.soap.SOAPBinding;
import java.sql.*;

import static fi.trustnet.browserextension.Configuration.USER_DB_FILE;

public class UserStore {
    private static final String TABLENAME = "users";
    private static int workload = 10;


    private static Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + USER_DB_FILE;

            try {
                conn = DriverManager.getConnection(url);
            } catch (SQLException e) {
                Logger.writeToLog(e.getMessage());
            }
        } catch (Exception e) {

        }
        return conn;
    }

    private static void createTables() {

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	username text UNIQUE NOT NULL,\n"
                + "	password text NOT NULL, \n"
                + "	walletname text NOT NULL \n"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            Logger.writeToLog(e.getMessage());        }
    }

    public static void initUserDb() {
        createTables();
    }

    public static void storeNewUser(UserAccount userAccount) {
        String sql = "INSERT INTO " + TABLENAME +" (username,password, walletname) VALUES(?,?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userAccount.getUsername());
            pstmt.setString(2, hashPassword(userAccount.getPassword()));
            pstmt.setString(3, userAccount.getWalletname());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Logger.writeToLog(e.getMessage());       }
    }

    public static UserAccount findUserByUsername(String name) {
        String sql = "SELECT * FROM " + TABLENAME + " WHERE username=\"" + name +"\"";
        UserAccount userAccount = null;
        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
             if(rs.next()) {
                userAccount = new UserAccount();
                userAccount.setUsername(rs.getString("username"));
                userAccount.setWalletname(rs.getString("walletname"));
                userAccount.setPassword(rs.getString("password"));
            }

        } catch (SQLException e) {
            Logger.writeToLog(e.getMessage());        }
        return userAccount;
    }

    public static String getWalletNameByUsername(String username) {
        UserAccount userAccount = findUserByUsername(username);
        if (userAccount != null) {
            return userAccount.getWalletname();
        }
        else {
            return null;
        }
    }

    public static void deleteUser(String username) {
        String sql = "DELETE FROM " + TABLENAME + " WHERE username=\"" +username + "\"";
        try (Connection conn = connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.executeUpdate();
        }

        catch (SQLException e) {
            Logger.writeToLog(e.getMessage());        }

    }
    public static boolean login(String username, String password) {
        UserAccount userAccount = findUserByUsername(username);
        if (checkPassword(password, userAccount.getPassword())) {
            return true;
        }
        else {
            return false;
        }
    }

    private static String hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);

        return(hashed_password);
    }

    private static boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            //throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
            Logger.writeToLog("checkPassword : Invalid hash provided for comparison");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return(password_verified);
    }

}
