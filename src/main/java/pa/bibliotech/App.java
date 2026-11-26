package pa.bibliotech;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pa.bibliotech.config.DatabaseConnection;
import pa.bibliotech.domain.User;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.repository.jdbc.JdbcBookRepository;
import pa.bibliotech.repository.jdbc.JdbcLoanRepository;
import pa.bibliotech.repository.jdbc.JdbcMemberRepository;
import pa.bibliotech.repository.jdbc.JdbcReportRepository;
import pa.bibliotech.repository.jdbc.JdbcUserRepository;
import pa.bibliotech.service.AuthService;
import pa.bibliotech.service.BookService;
import pa.bibliotech.service.LoanService;
import pa.bibliotech.service.MemberService;
import pa.bibliotech.service.ReportService;
import pa.bibliotech.strategy.CappedFineStrategy;
import pa.bibliotech.strategy.FineStrategy;
import pa.bibliotech.ui.BookController;
import pa.bibliotech.ui.LoanController;
import pa.bibliotech.ui.LoginController;
import pa.bibliotech.ui.MainMenuController;
import pa.bibliotech.ui.MemberController;
import pa.bibliotech.ui.ReportController;

public class App extends Application {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final FineStrategy FINE_STRATEGY =
            new CappedFineStrategy(new BigDecimal("0.50"), new BigDecimal("10.00"));
    private static final String OFFLINE_MESSAGE =
            "No hay conexión con el servidor. Revise su conexión a internet e intente de nuevo.";
    private Stage primaryStage;
    private User currentUser;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showLoginView();
    }

    private Connection connection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    private void showLoginView() {
        try {
            AuthService authService = new AuthService(new JdbcUserRepository(connection()));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pa/bibliotech/ui/login.fxml"));
            loader.setControllerFactory(
                    type -> new LoginController(authService, this::showMainMenuView, this::showLoginView));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("BiblioTech Panamá · Iniciar Sesión");
            primaryStage.show();
        } catch (DataAccessException | IllegalStateException e) {
            showOfflineView();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir el login", e);
        }
    }

    private void showOfflineView() {
        Label message = new Label(OFFLINE_MESSAGE);
        message.setWrapText(true);
        Button retry = new Button("Reintentar");
        retry.setOnAction(event -> showLoginView());
        VBox box = new VBox(16, message, retry);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24));
        primaryStage.setScene(new Scene(box, 420, 200));
        primaryStage.setTitle("BiblioTech Panamá · Sin conexión");
        primaryStage.show();
    }

    private void showConnectionError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Sin conexión");
        alert.setHeaderText(null);
        alert.setContentText(OFFLINE_MESSAGE);
        alert.showAndWait();
    }

    private void showMainMenuView(User user) {
        if (user != null) {
            currentUser = user;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pa/bibliotech/ui/main-menu.fxml"));
            loader.setControllerFactory(type -> new MainMenuController(currentUser, this::showBooksView,
                    this::showMembersView, this::showLoansView, this::showReportsView, this::logout));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("BiblioTech Panamá · Menú Principal");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir el menú principal", e);
        }
    }

    private void showBooksView() {
        try {
            BookService bookService = new BookService(new JdbcBookRepository(connection()));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pa/bibliotech/ui/book.fxml"));
            loader.setControllerFactory(type -> new BookController(bookService, () -> showMainMenuView(currentUser)));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("BiblioTech Panamá · Libros");
        } catch (DataAccessException e) {
            showConnectionError();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir libros", e);
        }
    }

    private void showMembersView() {
        try {
            MemberService memberService = new MemberService(new JdbcMemberRepository(connection()));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pa/bibliotech/ui/member.fxml"));
            loader.setControllerFactory(
                    type -> new MemberController(memberService, () -> showMainMenuView(currentUser)));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("BiblioTech Panamá · Socios");
        } catch (DataAccessException e) {
            showConnectionError();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir socios", e);
        }
    }

    private void showLoansView() {
        try {
            Connection connection = connection();
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
        } catch (DataAccessException e) {
            showConnectionError();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir préstamos", e);
        }
    }

    private void showReportsView() {
        try {
            ReportService reportService = new ReportService(new JdbcReportRepository(connection()));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pa/bibliotech/ui/report.fxml"));
            loader.setControllerFactory(
                    type -> new ReportController(reportService, () -> showMainMenuView(currentUser)));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("BiblioTech Panamá · Reportes");
        } catch (DataAccessException e) {
            showConnectionError();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo abrir reportes", e);
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