package pa.bibliotech.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import pa.bibliotech.domain.User;

public class MainMenuController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button reportsButton;

    private final User user;
    private final Runnable openBooksAction;
    private final Runnable openMembersAction;
    private final Runnable openLoansAction;
    private final Runnable openReportsAction;
    private final Runnable logoutAction;

    public MainMenuController(User user, Runnable openBooksAction, Runnable openMembersAction,
            Runnable openLoansAction, Runnable openReportsAction, Runnable logoutAction) {
        this.user = user;
        this.openBooksAction = openBooksAction;
        this.openMembersAction = openMembersAction;
        this.openLoansAction = openLoansAction;
        this.openReportsAction = openReportsAction;
        this.logoutAction = logoutAction;
    }

    @FXML
    private void initialize() {
        welcomeLabel.setText("Bienvenido, " + user.getFullName() + " (" + user.getRole() + ")");
        boolean admin = "ADMIN".equals(user.getRole());
        reportsButton.setVisible(admin);
        reportsButton.setManaged(admin);
    }

    @FXML
    private void handleOpenBooks() {
        openBooksAction.run();
    }

    @FXML
    private void handleOpenMembers() {
        openMembersAction.run();
    }

    @FXML
    private void handleOpenLoans() {
        openLoansAction.run();
    }

    @FXML
    private void handleOpenReports() {
        openReportsAction.run();
    }

    @FXML
    private void handleLogout() {
        logoutAction.run();
    }
}