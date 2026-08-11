package gh.dso.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central place to open a JDBC connection to the MySQL database.
 * Edit the constants below (or externalise into config.local.properties,
 * which is gitignored) to match your local MySQL setup.
 */
public final class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/ghana_courier_dso?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Khojoe@0107"; // do not commit real credentials

    private DatabaseConnection() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
