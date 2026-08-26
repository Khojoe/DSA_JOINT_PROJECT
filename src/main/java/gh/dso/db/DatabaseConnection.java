package gh.dso.db;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Central JDBC connection configured through a local .env file. */
public final class DatabaseConnection {

    private static final Dotenv DOTENV = loadDotenv();

    private static final String URL = env("DB_URL",
            "jdbc:mysql://localhost:3306/ghana_courier_dso?useSSL=false&serverTimezone=UTC");
    private static final String USER = env("DB_USER", "root");
    private static final String PASSWORD = env("DB_PASSWORD", "");

    private DatabaseConnection() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static Dotenv loadDotenv() {
        try {
            return Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception e) {
            System.err.println("[WARN] Could not load .env file: " + e.getMessage());
            return null;
        }
    }

    private static String env(String key, String defaultValue) {
        String value = DOTENV == null ? null : DOTENV.get(key);
        if (value == null || value.isBlank()) value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
