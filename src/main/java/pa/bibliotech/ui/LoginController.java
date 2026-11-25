package pa.bibliotech.ui;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pa.bibliotech.domain.User;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.service.AuthService;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final AuthService authService;
    private final java.util.function.Consumer<User> onLoginSuccess;
    private final Runnable onConnectionError;

    public LoginController(AuthService authService, java.util.function.Consumer<User> onLoginSuccess,
            Runnable onConnectionError) {
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;
        this.onConnectionError = onConnectionError;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Por favor ingrese usuario y contraseña.");
            return;
        }

        try {
            Optional<User> authenticatedUser = authService.authenticate(username, password);
            if (authenticatedUser.isPresent()) {
                onLoginSuccess.accept(authenticatedUser.get());
            } else {
                showAlert("Error de autenticación", "Usuario o contraseña incorrectos.");
            }
        } catch (DataAccessException e) {
            showAlert("Error de conexión",
                    "No se pudo conectar con el servidor. Revise su conexión a internet e intente de nuevo.");
            onConnectionError.run();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}