package pa.bibliotech;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import pa.bibliotech.config.DatabaseConnection;

public class App extends Application {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage stage) {
        String message;
        // Se cierran Statement y ResultSet, NUNCA la conexión del Singleton
        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT 1")) {
            rs.next();
            message = "Conexión a Supabase OK";
            LOGGER.info(message);
        } catch (SQLException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "No se pudo conectar a la base de datos", e);
            message = "Error de conexión: revise application.properties";
        }
        stage.setScene(new Scene(new Label(message), 380, 120));
        stage.setTitle("BiblioTech Panamá");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}