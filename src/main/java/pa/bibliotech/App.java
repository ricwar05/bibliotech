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
import pa.bibliotech.repository.jdbc.JdbcBookRepository;
import pa.bibliotech.service.BookService;
import pa.bibliotech.ui.BookController;

/** Arranque provisional: abre la pantalla de libros. Pedro lo reemplaza al integrar el login. */
public class App extends Application {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage stage) {
        Connection connection;
        try {
            connection = DatabaseConnection.getInstance().getConnection();   // Singleton
            LOGGER.info("Conexión a Supabase OK");
        } catch (SQLException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "No se pudo conectar a la base de datos", e);
            stage.setScene(new Scene(new Label("Error de conexión: revise application.properties"), 380, 120));
            stage.show();
            return;
        }

        // Armado: el repositorio JDBC entra al servicio por su interfaz
        BookService bookService = new BookService(new JdbcBookRepository(connection));

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("ui/book.fxml"));
            loader.setControllerFactory(type -> new BookController(bookService, () -> { }));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("BiblioTech Panamá · Libros");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir la pantalla de libros", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}