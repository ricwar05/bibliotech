package pa.bibliotech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Patrón Singleton: una sola conexión para toda la aplicación. */
public final class DatabaseConnection {
    private static DatabaseConnection instance;
    private final Connection connection;

    private DatabaseConnection() throws SQLException {   // privado: nadie puede hacer "new"
        String url = AppConfig.get("db.url");
        String user = AppConfig.get("db.user");
        String password = AppConfig.get("db.password");
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {                          // se crea solo la primera vez
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}