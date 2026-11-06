package pa.bibliotech;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import pa.bibliotech.config.DatabaseConnection;

public class App extends Application {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage stage) {
        Connection connection;
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            LOGGER.info("Conexión a Supabase OK");
        } catch (SQLException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "No se pudo conectar a la base de datos", e);
            stage.setScene(new Scene(new Label("Error de conexión: revise application.properties"), 380, 120));
            stage.show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/pa/bibliotech/ui/login.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("BiblioTech Panamá · Iniciar Sesión");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir la pantalla de login", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}