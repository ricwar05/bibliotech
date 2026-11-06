package pa.bibliotech.service;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import pa.bibliotech.domain.User;
import pa.bibliotech.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isActive()
                    && (password.equals(user.getPasswordHash()) || BCrypt.checkpw(password, user.getPasswordHash()))) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}