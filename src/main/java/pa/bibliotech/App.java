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
import pa.bibliotech.domain.User;
import pa.bibliotech.repository.jdbc.JdbcBookRepository;
import pa.bibliotech.repository.jdbc.JdbcMemberRepository;
import pa.bibliotech.repository.jdbc.JdbcUserRepository;
import pa.bibliotech.service.AuthService;
import pa.bibliotech.service.BookService;
import pa.bibliotech.service.MemberService;
import pa.bibliotech.ui.BookController;
import pa.bibliotech.ui.LoginController;
import pa.bibliotech.ui.MainMenuController;
import pa.bibliotech.ui.MemberController;

public class App extends Application {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private Connection connection;
    private Stage primaryStage;
    private User currentUser;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            LOGGER.info("Conexión a Supabase OK");
        } catch (SQLException | IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "No se pudo conectar a la base de datos", e);
            stage.setScene(new Scene(new Label("Error de conexión: revise application.properties"), 380, 120));
            stage.show();
            return;
        }

        showLoginView();
    }

    private void showLoginView() {
        try {
            AuthService authService = new AuthService(new JdbcUserRepository(connection));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pa/bibliotech/ui/login.fxml"));
            loader.setControllerFactory(type -> new LoginController(authService, this::showMainMenuView));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("BiblioTech Panamá · Iniciar Sesión");
            primaryStage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir el login", e);
        }
    }

    private void showMainMenuView(User user) {
        if (user != null) {
            currentUser = user;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pa/bibliotech/ui/main-menu.fxml"));
            loader.setControllerFactory(type -> new MainMenuController(currentUser, this::showBooksView,
                    this::showMembersView, this::logout));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("BiblioTech Panamá · Menú Principal");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir el menú principal", e);
        }
    }

    private void showBooksView() {
        try {
            BookService bookService = new BookService(new JdbcBookRepository(connection));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pa/bibliotech/ui/book.fxml"));
            loader.setControllerFactory(type -> new BookController(bookService, () -> showMainMenuView(currentUser)));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("BiblioTech Panamá · Libros");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir libros", e);
        }
    }

    private void showMembersView() {
        try {
            MemberService memberService = new MemberService(new JdbcMemberRepository(connection));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pa/bibliotech/ui/member.fxml"));
            loader.setControllerFactory(
                    type -> new MemberController(memberService, () -> showMainMenuView(currentUser)));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("BiblioTech Panamá · Socios");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir socios", e);
        }
    }

    private void logout() {
        currentUser = null;
        showLoginView();
    }

    public static void main(String[] args) {
        launch(args);
    }
}