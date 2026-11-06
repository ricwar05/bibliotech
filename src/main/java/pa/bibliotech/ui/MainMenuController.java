package pa.bibliotech.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pa.bibliotech.domain.User;

public class MainMenuController {

    @FXML
    private Label welcomeLabel;

    private final Runnable openBooksAction;
    private final Runnable openMembersAction;

    public MainMenuController(User user, Runnable openBooksAction, Runnable openMembersAction) {
        this.openBooksAction = openBooksAction;
        this.openMembersAction = openMembersAction;
    }

    @FXML
    private void handleOpenBooks() {
        if (openBooksAction != null)
            openBooksAction.run();
    }

    @FXML
    private void handleOpenMembers() {
        if (openMembersAction != null)
            openMembersAction.run();
    }
}