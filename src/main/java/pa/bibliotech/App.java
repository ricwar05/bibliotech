package pa.bibliotech;

import java.io.IOException;
import java.math.BigDecimal;
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
import pa.bibliotech.repository.jdbc.JdbcLoanRepository;
import pa.bibliotech.repository.jdbc.JdbcMemberRepository;
import pa.bibliotech.repository.jdbc.JdbcUserRepository;
import pa.bibliotech.service.AuthService;
import pa.bibliotech.service.BookService;
import pa.bibliotech.service.LoanService;
import pa.bibliotech.service.MemberService;
import pa.bibliotech.strategy.CappedFineStrategy;
import pa.bibliotech.strategy.FineStrategy;
import pa.bibliotech.ui.BookController;
import pa.bibliotech.ui.LoanController;
import pa.bibliotech.ui.LoginController;
import pa.bibliotech.ui.MainMenuController;
import pa.bibliotech.ui.MemberController;

public class App extends Application {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final FineStrategy FINE_STRATEGY =
            new CappedFineStrategy(new BigDecimal("0.50"), new BigDecimal("10.00"));
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
                    this::showMembersView, this::showLoansView, this::logout));
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

    private void showLoansView() {
        try {
            BookService bookService = new BookService(new JdbcBookRepository(connection));
            MemberService memberService = new MemberService(new JdbcMemberRepository(connection));
            LoanService loanService = new LoanService(new JdbcLoanRepository(connection),
                    new JdbcBookRepository(connection), new JdbcMemberRepository(connection),
                    FINE_STRATEGY, connection);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pa/bibliotech/ui/loan.fxml"));
            loader.setControllerFactory(type -> new LoanController(loanService, bookService, memberService,
                    () -> showMainMenuView(currentUser)));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("BiblioTech Panamá · Préstamos");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir préstamos", e);
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