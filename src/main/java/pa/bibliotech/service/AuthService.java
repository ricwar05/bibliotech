package pa.bibliotech.service;

import java.util.Optional;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import pa.bibliotech.domain.User;
import pa.bibliotech.repository.UserRepository;

public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isActive() && BCrypt.checkpw(password, user.getPasswordHash())) {
                LOGGER.info("Inicio de sesión exitoso: " + user.getUsername());
                return Optional.of(user);
            }
        }
        LOGGER.warning("Intento de acceso fallido para: " + username);
        return Optional.empty();
    }
}