package gh.dso.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Central place to open a JDBC connection to the MySQL database.
 * Loads settings from config.local.properties (gitignored) or environment variables.
 */
public final class DatabaseConnection {

    private static String url =
            "jdbc:mysql://localhost:3306/ghana_courier_dso?useSSL=false&serverTimezone=UTC";
    private static String user = "root";
    private static String password = ""; // default to empty for local MySQL defaults

    static {
        Properties props = new Properties();
        
        // 1. Try loading config from file system (working directory)
        try (InputStream input = new FileInputStream("config.local.properties")) {
            props.load(input);
        } catch (IOException e) {
            // 2. Try loading config from classpath as fallback
            try (InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("config.local.properties")) {
                if (input != null) {
                    props.load(input);
                }
            } catch (IOException e1) {
                // Ignore and use default properties
            }
        }

        // Apply properties if defined
        url = props.getProperty("db.url", url);
        user = props.getProperty("db.user", user);
        password = props.getProperty("db.password", password);

        // 3. Environment variables take precedence if present
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPassword = System.getenv("DB_PASSWORD");
        
        if (envUrl != null && !envUrl.isEmpty()) {
            url = envUrl;
        }
        if (envUser != null && !envUser.isEmpty()) {
            user = envUser;
        }
        if (envPassword != null && !envPassword.isEmpty()) {
            password = envPassword;
        }
    }

    private DatabaseConnection() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
