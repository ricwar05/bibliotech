package pa.bibliotech.service;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import pa.bibliotech.domain.User;
import pa.bibliotech.exception.AuthenticationException;
import pa.bibliotech.exception.BusinessRuleException;
import pa.bibliotech.repository.UserRepository;

public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private static final String INVALID_MSG = "Usuario o contraseña incorrectos";
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            throw new BusinessRuleException("Ingrese usuario y contraseña");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BusinessRuleException("La contraseña es demasiado larga");
        }

        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new AuthenticationException(INVALID_MSG));

        if (!user.isActive() || !BCrypt.checkpw(password, user.getPasswordHash())) {
            LOGGER.warning("Intento de acceso fallido para: " + username);
            throw new AuthenticationException(INVALID_MSG);
        }

        LOGGER.info("Inicio de sesión exitoso: " + username);
        return user;
    }
}