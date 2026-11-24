package pa.bibliotech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import pa.bibliotech.exception.DataAccessException;

public final class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final int VALIDATION_TIMEOUT_SECONDS = 3;
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || !connection.isValid(VALIDATION_TIMEOUT_SECONDS)) {
                closeQuietly();
                connection = DriverManager.getConnection(
                        AppConfig.get("db.url"), AppConfig.get("db.user"), AppConfig.get("db.password"));
                LOGGER.info("Conexión a la base de datos abierta");
            }
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "No se pudo conectar a la base de datos", e);
            throw new DataAccessException("No hay conexión con la base de datos", e);
        }
    }

    private void closeQuietly() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.log(Level.FINE, "La conexión anterior ya estaba cerrada", e);
        }
        connection = null;
    }
}