package pa.bibliotech.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import pa.bibliotech.domain.User;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.repository.UserRepository;

public class JdbcUserRepository implements UserRepository {
    private static final Logger LOGGER = Logger.getLogger(JdbcUserRepository.class.getName());
    private final Connection connection;

    public JdbcUserRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM app_users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("full_name"),
                            rs.getString("role"),
                            rs.getBoolean("active"));
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar usuario: " + username, e);
            throw new DataAccessException("No se pudo consultar el usuario", e);
        }
    }
}