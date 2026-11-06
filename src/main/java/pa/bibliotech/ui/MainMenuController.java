package pa.bibliotech.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainMenuController {

    @FXML
    private Label welcomeLabel;

    public void setUsername(String username) {
        welcomeLabel.setText("Menú Principal - Usuario: " + username);
    }
}