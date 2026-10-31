package pa.bibliotech.repository;

import java.util.Optional;

import pa.bibliotech.domain.User;

public interface UserRepository {
    Optional<User> findByUsername(String username);
}