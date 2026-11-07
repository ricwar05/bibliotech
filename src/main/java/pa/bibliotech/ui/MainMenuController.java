package pa.bibliotech.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import pa.bibliotech.domain.User;

public class MainMenuController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button usersButton;

    private final User user;
    private final Runnable openBooksAction;
    private final Runnable openMembersAction;
    private final Runnable logoutAction;

    public MainMenuController(User user, Runnable openBooksAction, Runnable openMembersAction, Runnable logoutAction) {
        this.user = user;
        this.openBooksAction = openBooksAction;
        this.openMembersAction = openMembersAction;
        this.logoutAction = logoutAction;
    }

    @FXML
    private void initialize() {
        welcomeLabel.setText("Bienvenido, " + user.getFullName() + " (" + user.getRole() + ")");
        boolean admin = "ADMIN".equals(user.getRole());
        usersButton.setVisible(admin);
        usersButton.setManaged(admin);
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
    private void handleUsers() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Usuarios");
        alert.setHeaderText(null);
        alert.setContentText("La gestión de usuarios se agrega en una fase siguiente.");
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        logoutAction.run();
    }
}